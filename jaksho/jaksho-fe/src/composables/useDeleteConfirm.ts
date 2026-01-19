/**
 * useDeleteConfirm - 删除确认通用逻辑
 * 处理删除操作的二次确认、API 调用和结果反馈
 */

import { DialogPlugin, MessagePlugin } from 'tdesign-vue-next'
import type { DeleteConfirmConfig } from '@/types/common'
import type { UseDeleteConfirmOptions, UseDeleteConfirmReturn } from '@/types/composables'

/**
 * 删除确认 Composable
 * @param options 配置选项
 * @returns 删除确认相关方法
 */
export function useDeleteConfirm(options: UseDeleteConfirmOptions = {}): UseDeleteConfirmReturn {
  const { defaultConfig = {}, onSuccess, onError } = options

  /**
   * 显示删除确认对话框
   * @param config 确认配置
   * @returns 用户是否确认删除
   */
  const confirm = async (config: DeleteConfirmConfig = {}): Promise<boolean> => {
    const finalConfig = {
      title: '确认删除',
      content: '删除后数据将无法恢复，确定要删除吗？',
      theme: 'warning',
      confirmText: '确认',
      cancelText: '取消',
      ...defaultConfig,
      ...config,
    } as const

    return new Promise((resolve) => {
      const dialog = DialogPlugin.confirm({
        header: finalConfig.title,
        body: finalConfig.content,
        theme: finalConfig.theme,
        confirmBtn: finalConfig.confirmText,
        cancelBtn: finalConfig.cancelText,
        onConfirm: () => {
          dialog.destroy()
          resolve(true)
        },
        onClose: () => {
          dialog.destroy()
          resolve(false)
        },
      })
    })
  }

  /**
   * 删除处理（带确认）
   * @param item 要删除的项
   * @param deleteApi 删除 API 函数
   * @param config 确认配置
   * @returns 是否删除成功
   */
  const handleDelete = async (
    item: any,
    deleteApi: (id: any) => Promise<void>,
    config?: DeleteConfirmConfig,
  ): Promise<boolean> => {
    // 显示确认对话框
    const confirmed = await confirm(config)
    if (!confirmed) {
      return false
    }

    try {
      // 调用删除 API
      const id = item?.id ?? item
      await deleteApi(id)

      // 成功提示
      await MessagePlugin.success('删除成功')
      onSuccess?.(item)

      return true
    } catch (err) {
      console.error('[useDeleteConfirm] 删除失败:', err)

      // 失败提示
      await MessagePlugin.error('删除失败，请重试')
      onError?.(err as Error)

      return false
    }
  }

  /**
   * 批量删除处理（带确认）
   * @param items 要删除的项列表
   * @param deleteApi 批量删除 API 函数
   * @param config 确认配置
   * @returns 是否删除成功
   */
  const handleBatchDelete = async (
    items: any[],
    deleteApi: (ids: any[]) => Promise<void>,
    config?: DeleteConfirmConfig,
  ): Promise<boolean> => {
    if (!items || items.length === 0) {
      await MessagePlugin.warning('请选择要删除的项')
      return false
    }

    // 自定义批量删除的确认内容
    const batchConfig: DeleteConfirmConfig = {
      title: '确认批量删除',
      content: `确定要删除选中的 ${items.length} 项吗？删除后数据将无法恢复。`,
      theme: 'warning',
      ...config,
    }

    // 显示确认对话框
    const confirmed = await confirm(batchConfig)
    if (!confirmed) {
      return false
    }

    try {
      // 提取 ID 列表
      const ids = items.map((item) => item?.id ?? item)

      // 调用批量删除 API
      await deleteApi(ids)

      // 成功提示
      await MessagePlugin.success(`成功删除 ${items.length} 项`)
      onSuccess?.(items)

      return true
    } catch (err) {
      console.error('[useDeleteConfirm] 批量删除失败:', err)

      // 失败提示
      await MessagePlugin.error('批量删除失败，请重试')
      onError?.(err as Error)

      return false
    }
  }

  return {
    confirm,
    handleDelete,
    handleBatchDelete,
  }
}

