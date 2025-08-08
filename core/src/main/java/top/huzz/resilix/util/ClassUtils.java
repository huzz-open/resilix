package top.huzz.resilix.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import jakarta.annotation.Nonnull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author huzz
 * @since 1.0.2
 */
public final class ClassUtils {

    /**
     * 获取一个类的所有具体泛型类型，包括父类和接口
     *
     * @param clazz 类
     * @return 具体泛型类型列表
     */
    public static List<Type> getConcreteGenericTypes(Class<?> clazz) {
        // 检查父类
        Type superType = clazz.getGenericSuperclass();
        List<Type> result = new ArrayList<>(getConcreteGenericTypesFromType(superType));
        // 检查所有接口
        for (Type interfaceType : clazz.getGenericInterfaces()) {
            result.addAll(getConcreteGenericTypesFromType(interfaceType));
        }
        return result;
    }

    /**
     * 递归获取一个类型的具体泛型类型
     *
     * @param type 类型
     * @return 具体泛型类型列表
     */
    private static List<Type> getConcreteGenericTypesFromType(Type type) {
        List<Type> result = new ArrayList<>();
        if (type instanceof ParameterizedType parameterizedType) {
            for (Type arg : parameterizedType.getActualTypeArguments()) {
                if (arg instanceof Class<?> || arg instanceof ParameterizedType) {
                    result.add(arg);
                    result.addAll(getConcreteGenericTypesFromType(arg));
                }
                // 跳过 TypeVariable
            }
        }
        return result;
    }

    /**
     * 处理一个类的子类
     *
     * @param clazz            父类
     * @param classInfoHandler 类信息处理器
     */
    public static void doWithSubClass(@Nonnull Class<?> clazz, @Nonnull Consumer<ClassInfo> classInfoHandler, String... scanPackages) {
        doWith(clazz, ScanResult::getSubclasses, classInfoHandler, scanPackages, ClassInfoList::getStandardClasses);
    }


    /**
     * 处理接口的实现枚举实现类
     *
     * @param clazz            接口
     * @param classInfoHandler 类信息处理器
     * @param scanPackages     扫描包路径
     * @see ClassInfoList
     */
    public static void doWithClassEnumImpl(@Nonnull Class<?> clazz, @Nonnull Consumer<ClassInfo> classInfoHandler, String... scanPackages) {
        doWithClassImpl(clazz, classInfoHandler, scanPackages, ClassInfoList::getEnums);
    }

    /**
     * 处理接口的实现类
     *
     * @param clazz            接口
     * @param classInfoHandler 类信息处理器
     * @param scanPackages     扫描包路径
     * @param subFunctions     截取函数，如果只需要类，可以使用{@link ClassInfoList#getStandardClasses()}，如果只需要枚举，可以使用{@link ClassInfoList#getEnums()}
     * @see ClassInfoList
     */
    @SafeVarargs
    public static void doWithClassImpl(@Nonnull Class<?> clazz, @Nonnull Consumer<ClassInfo> classInfoHandler, String[] scanPackages, Function<ClassInfoList, ClassInfoList>... subFunctions) {
        doWith(clazz, ScanResult::getClassesImplementing, classInfoHandler, scanPackages, subFunctions);
    }


    /**
     * @param clazz            父类或者接口
     * @param resultHandler    扫描结果处理器，如果给定{@link ScanResult#getSubclasses(String)}则表示只获取其子类；如果给定果给定{@link ScanResult#getClassesImplementing(String)} (String)}则表示只获取其接口实现类，
     * @param classInfoHandler 类信息处理器
     * @param scanPackages     扫描包路径
     * @param subFunctions     截取函数，如果只需要子类，可以使用{@link ClassInfoList#getStandardClasses()}，如果只需要枚举类，可以使用{@link ClassInfoList#getEnums()}
     * @see ScanResult
     * @see ClassInfoList
     */
    @SafeVarargs
    private static void doWith(@Nonnull Class<?> clazz,
                               @Nonnull BiFunction<ScanResult, String, ClassInfoList> resultHandler,
                               @Nonnull Consumer<ClassInfo> classInfoHandler,
                               String[] scanPackages,
                               Function<ClassInfoList, ClassInfoList>... subFunctions) {
        Objects.requireNonNull(classInfoHandler);

        ClassInfoList infoList = getClassInfoList(clazz, resultHandler, scanPackages);

        if (infoList.isEmpty()) {
            return;
        }
        if (subFunctions != null) {
            for (Function<ClassInfoList, ClassInfoList> subFunction : subFunctions) {
                infoList = subFunction.apply(infoList);
            }
        }

        for (ClassInfo subClassInfo : infoList) {
            classInfoHandler.accept(subClassInfo);
        }
    }


    /**
     * 获取类的子类
     *
     * @param clazz         父类或者接口
     * @param resultHandler 扫描结果处理器，如果给定{@link ScanResult#getSubclasses(String)}则表示只获取其子类；如果给定果给定{@link ScanResult#getClassesImplementing(String)} (String)}则表示只获取其接口实现类，
     * @param scanPackages  扫描包路径
     * @return 子类或者接口的实现类
     */
    @Nonnull
    private static ClassInfoList getClassInfoList(@Nonnull Class<?> clazz, @Nonnull BiFunction<ScanResult, String, ClassInfoList> resultHandler, String[] scanPackages) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(resultHandler);
        // 设置扫描包路径，加快启动顺序
        try (ScanResult scanResult = new ClassGraph().acceptPackages(scanPackages).enableAllInfo().scan()) {
            return resultHandler.apply(scanResult, clazz.getCanonicalName());
        }
    }
}
