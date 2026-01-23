/**
 * 字段属性工具类（位标识）
 * 使用二进制的每一位来表示该字段的属性：
 *   [1]（低1位）：表示该字段是否可以被用于HTTP接口输入参数。1表示可以，0表示不可以。
 *   [2]：表示该字段是否可以被用于HTTP接口输出参数。1表示可以，0表示不可以。
 *   [3]~[8]：预留位。
 * 
 * 示例：
 *   1 表示字段仅仅只能作为接口输入。
 *   2 表示字段仅仅只能作为接口输出。
 *   3 表示该字段既可以作为接口输入也可以作为输出。
 *
 * @author system
 * @since 1.0.3
 */
export const FieldAttribute = {
  INPUT: 1,   // 0b00000001: 可作为输入
  OUTPUT: 2,  // 0b00000010: 可作为输出
  BOTH: 3,    // 0b00000011: 输入输出均可
  DEFAULT: 3, // 默认值

  /**
   * 检查是否包含指定属性
   */
  hasAttribute(value: number, flag: number): boolean {
    return (value & flag) !== 0;
  },

  /**
   * 是否可作为输入
   */
  isInput(value: number): boolean {
    return this.hasAttribute(value, this.INPUT);
  },

  /**
   * 是否可作为输出
   */
  isOutput(value: number): boolean {
    return this.hasAttribute(value, this.OUTPUT);
  },

  /**
   * 获取描述
   */
  getDescription(value: number): string {
    if (value === this.BOTH) return '输入/输出';
    if (value === this.INPUT) return '仅输入';
    if (value === this.OUTPUT) return '仅输出';
    return '未知';
  },

  /**
   * 获取标签主题
   */
  getTagTheme(value: number): string {
    if (value === this.INPUT) return 'primary';
    if (value === this.OUTPUT) return 'success';
    return 'default';
  },

  /**
   * 将数字值转换为复选框数组
   * @param value 字段属性值
   * @returns 复选框数组
   */
  toCheckboxArray(value: number): number[] {
    const result: number[] = [];
    if (this.isInput(value)) result.push(this.INPUT);
    if (this.isOutput(value)) result.push(this.OUTPUT);
    return result.length > 0 ? result : [this.INPUT, this.OUTPUT]; // 默认全选
  },

  /**
   * 将复选框数组转换为数字值（位运算）
   * @param checkboxValues 复选框数组
   * @returns 字段属性值
   */
  fromCheckboxArray(checkboxValues: number[]): number {
    if (!checkboxValues || checkboxValues.length === 0) return this.DEFAULT;
    return checkboxValues.reduce((acc, cur) => acc | cur, 0);
  },
};
