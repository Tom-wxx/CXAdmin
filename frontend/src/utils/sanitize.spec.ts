import { describe, expect, it } from 'vitest'
import { sanitizeHtml } from './sanitize'

describe('sanitizeHtml', () => {
  it('removes scripts, inline event handlers and javascript: links', () => {
    const dirty = '<p onclick="alert(1)">hi</p><script>alert(2)</script><a href="javascript:alert(3)">x</a><img src=x onerror="alert(4)">'
    const clean = sanitizeHtml(dirty)

    expect(clean).not.toContain('<script')
    expect(clean).not.toContain('onclick')
    expect(clean).not.toContain('onerror')
    expect(clean).not.toContain('javascript:')
    expect(clean).toContain('<p>hi</p>')
  })

  it('keeps Quill formatting (class/style)', () => {
    const html = '<p class="ql-align-center"><strong style="color: red;">公告</strong></p>'
    expect(sanitizeHtml(html)).toBe(html)
  })

  it('returns empty string for null/undefined', () => {
    expect(sanitizeHtml(null)).toBe('')
    expect(sanitizeHtml(undefined)).toBe('')
  })
})
