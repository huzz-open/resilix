package top.huzz.jaksho.interceptor;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import top.huzz.jaksho.common.entity.Resp;

/**
 * @author huzz
 * @since 1.0.2
 */
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = Integer.MAX_VALUE)
public class TestFilter implements Filter, BaseFilter.Listener {

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        Object value = appResponse.getValue();
        appResponse.setValue(Resp.of(value));
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        System.out.println("TestFilter onError executed: " + t.getMessage());
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }
}
