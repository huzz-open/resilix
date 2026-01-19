/**
 * useDialogForm - 对话框表单通用逻辑
 * 处理对话框的打开/关闭、表单的提交/重置/验证等操作
 */

import { ref, reactive, computed, nextTick } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import type { DialogConfig } from '@/types/common'
import type { UseDialogFormOptions, UseDialogFormReturn } from '@/types/composables'

/**
 * 对话框表单 Composable
 * @param options 配置选项
 * @returns 对话框表单相关状态和方法
 */
export function useDialogForm<T extends Record<string, any> = any>(
  options: UseDialogFormOptions<T> = {},
): UseDialogFormReturn<T> {
  const { initialData, dialogConfig = {}, onSubmit, onClose, onOpen, validator } = options

  // ========== 响应式状态 ==========
  const mode = ref<'create' | 'edit' | 'detail'>('create')
  const submitting = ref(false)
  const formRef = ref<any>(null)

  // 对话框配置
  const internalDialogConfig = reactive<DialogConfig & { visible: boolean }>({
    visible: false,
    title: '',
    width: '600px',
    confirmBtn: true,
    cancelBtn: true,
    confirmLoading: false,
    destroyOnClose: true,
    closeOnOverlayClick: false,
    ...dialogConfig,
  })

  // 表单数据
  const getInitialData = (): T => {
    if (typeof initialData === 'function') {
      return initialData()
    }
    return initialData ? { ...initialData } : ({} as T)
  }

  const formData = ref<T>(getInitialData()) as any

  // ========== 计算属性 ==========
  const isReadonly = computed(() => mode.value === 'detail')
  const dialogTitle = computed(() => {
    if (internalDialogConfig.title) return internalDialogConfig.title

    const titleMap = {
      create: '新建',
      edit: '编辑',
      detail: '详情',
    }
    return titleMap[mode.value]
  })

  // ========== 方法 ==========
  /**
   * 打开对话框（新建模式）
   */
  const openCreate = () => {
    mode.value = 'create'
    formData.value = getInitialData()
    internalDialogConfig.visible = true
    internalDialogConfig.confirmBtn = true

    nextTick(() => {
      formRef.value?.clearValidate?.()
      onOpen?.('create')
    })
  }

  /**
   * 打开对话框（编辑模式）
   */
  const openEdit = (data: T) => {
    mode.value = 'edit'
    formData.value = { ...getInitialData(), ...data }
    internalDialogConfig.visible = true
    internalDialogConfig.confirmBtn = true

    nextTick(() => {
      formRef.value?.clearValidate?.()
      onOpen?.('edit', data)
    })
  }

  /**
   * 打开对话框（详情模式）
   */
  const openDetail = (data: T) => {
    mode.value = 'detail'
    formData.value = { ...data }
    internalDialogConfig.visible = true
    internalDialogConfig.confirmBtn = false

    nextTick(() => {
      onOpen?.('detail', data)
    })
  }

  /**
   * 关闭对话框
   */
  const close = () => {
    internalDialogConfig.visible = false
    submitting.value = false

    // 延迟重置表单，避免关闭动画时看到数据清空
    setTimeout(() => {
      formData.value = getInitialData()
      formRef.value?.clearValidate?.()
      onClose?.()
    }, 300)
  }

  /**
   * 验证表单
   */
  const validate = async (): Promise<boolean> => {
    if (!formRef.value) {
      console.warn('[useDialogForm] formRef 未设置')
      return true
    }

    try {
      // TDesign 表单验证
      const result = await formRef.value.validate?.()
      if (result !== true) {
        return false
      }

      // 自定义验证
      if (validator) {
        const customResult = await validator(formData.value)
        if (!customResult) {
          return false
        }
      }

      return true
    } catch (err) {
      console.error('[useDialogForm] 表单验证失败:', err)
      return false
    }
  }

  /**
   * 提交表单
   */
  const submit = async () => {
    // 详情模式不允许提交
    if (mode.value === 'detail') {
      close()
      return
    }

    // 验证表单
    const isValid = await validate()
    if (!isValid) {
      await MessagePlugin.warning('请检查表单填写是否正确')
      return
    }

    if (!onSubmit) {
      console.warn('[useDialogForm] 未提供 onSubmit 回调')
      close()
      return
    }

    submitting.value = true
    internalDialogConfig.confirmLoading = true

    try {
      await onSubmit(formData.value, mode.value as 'create' | 'edit')
      await MessagePlugin.success(mode.value === 'create' ? '创建成功' : '更新成功')
      close()
    } catch (err) {
      console.error('[useDialogForm] 提交失败:', err)
      await MessagePlugin.error(mode.value === 'create' ? '创建失败' : '更新失败')
    } finally {
      submitting.value = false
      internalDialogConfig.confirmLoading = false
    }
  }

  /**
   * 重置表单
   */
  const reset = () => {
    formData.value = getInitialData()
    nextTick(() => {
      formRef.value?.clearValidate?.()
    })
  }

  return {
    dialogConfig: internalDialogConfig as any,
    formData,
    formRef,
    submitting,
    openCreate,
    openEdit,
    openDetail,
    close,
    submit,
    reset,
    validate,
    mode,
  }
}

