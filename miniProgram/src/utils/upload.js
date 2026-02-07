/**
 * 图片上传：若后端提供上传接口则在此调用并返回 URL；
 * 否则可对接微信云开发/第三方存储，MVP 可先返回本地临时路径供占位。
 * @param {string} filePath 本地文件路径
 * @returns {Promise<string>} 图片 URL
 */
export function uploadImage(filePath) {
  // TODO: 对接后端上传接口或云存储
  return Promise.resolve(filePath)
}
