import DOMPurify from 'dompurify'

/**
 * 清洗将要通过 v-html 渲染的富文本（公告、通知内容），移除 script、事件属性、javascript: 链接等，
 * 防止存储型 XSS。保留 class/style，富文本编辑器（Quill）的排版不受影响。
 */
export function sanitizeHtml(html: string | null | undefined): string {
  return html ? DOMPurify.sanitize(html) : ''
}
