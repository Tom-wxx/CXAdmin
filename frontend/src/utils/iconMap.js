/**
 * 旧 Element UI 字体图标名 -> Element Plus SVG 组件名 的运行时解析。
 *
 * 背景：菜单图标存在数据库 sys_menu.icon（如 user / s-tools / time），
 * Vue2 时模板用 `<i class="el-icon-xxx">` 渲染。Element Plus 改为 SVG 组件，
 * 所有图标已在 main.js 全局注册（PascalCase）。本模块把历史名解析为
 * 已注册的组件名，无法解析时返回 null（渲染端 v-if 跳过，避免组件解析告警）。
 */
import * as ElementPlusIcons from '@element-plus/icons-vue'

// 实际可用的 Element Plus 图标组件名集合（PascalCase）
const EP_NAMES = new Set(Object.keys(ElementPlusIcons))

// Element UI 名称(kebab) -> Element Plus 组件名，仅列出不能直接 PascalCase 命中的（改名/合并/删除）
const RENAME = {
  'attract': 'Magnet',
  'bangzhu': 'Help',
  'bank-card': 'CreditCard',
  'c-scale-to-original': 'ScaleToOriginal',
  'camera-solid': 'CameraFilled',
  'circle-plus-outline': 'CirclePlus',
  'close-notification': 'MuteNotification',
  'cloudy-and-sunny': 'PartlyCloudy',
  'date': 'Calendar',
  'delete-solid': 'DeleteFilled',
  'dish-1': 'Dish',
  'discover': 'Compass',
  'edit-outline': 'EditPen',
  'error': 'CircleCloseFilled',
  'heavy-rain': 'Pouring',
  'info': 'InfoFilled',
  'light-rain': 'Drizzling',
  'location-outline': 'Location',
  'medal-1': 'Medal',
  'message-solid': 'Message',
  'mobile': 'Iphone',
  'mobile-phone': 'Cellphone',
  'more-outline': 'MoreFilled',
  'news': 'Document',
  'notebook-1': 'Notebook',
  'notebook-2': 'Notebook',
  'phone-outline': 'Phone',
  'picture-outline': 'Picture',
  'picture-outline-round': 'PictureRounded',
  'platform-eleme': 'Eleme',
  'potato-strips': 'Fries',
  'question': 'QuestionFilled',
  'receiving': 'Box',
  'remove-outline': 'Remove',
  's-check': 'CircleCheckFilled',
  's-claim': 'DocumentChecked',
  's-comment': 'ChatDotSquare',
  's-cooperation': 'Help',
  's-custom': 'UserFilled',
  's-data': 'Histogram',
  's-finance': 'Money',
  's-flag': 'Flag',
  's-fold': 'Fold',
  's-goods': 'GoodsFilled',
  's-grid': 'Grid',
  's-help': 'Service',
  's-home': 'HomeFilled',
  's-management': 'Management',
  's-marketing': 'TrendCharts',
  's-open': 'FolderOpened',
  's-operation': 'Operation',
  's-opportunity': 'Opportunity',
  's-order': 'Tickets',
  's-platform': 'Platform',
  's-promotion': 'Promotion',
  's-release': 'Position',
  's-shop': 'Shop',
  's-ticket': 'Ticket',
  's-tools': 'Tools',
  's-unfold': 'Expand',
  'scissors': 'Scissor',
  'shopping-bag-1': 'ShoppingBag',
  'shopping-bag-2': 'ShoppingBag',
  'shopping-cart-1': 'ShoppingCart',
  'shopping-cart-2': 'ShoppingCart',
  'star-off': 'Star',
  'star-on': 'StarFilled',
  'success': 'SuccessFilled',
  'suitcase-1': 'SuitcaseLine',
  'sunrise-1': 'Sunrise',
  'table-lamp': 'ReadingLamp',
  'tableware': 'KnifeFork',
  'thumb': 'Pointer',
  'time': 'Clock',
  'trophy-1': 'TrophyBase',
  'truck': 'Van',
  'turn-off-microphone': 'Mute',
  'upload2': 'Upload',
  'user-solid': 'UserFilled',
  'video-camera-solid': 'VideoCameraFilled',
  'warning-outline': 'Warning',
  'watch-1': 'QuartzWatch',
  'water-cup': 'Mug'
}

function toPascal(kebab) {
  return kebab
    .split('-')
    .filter(Boolean)
    .map(s => s.charAt(0).toUpperCase() + s.slice(1))
    .join('')
}

/**
 * 把任意历史图标名解析为已注册的 Element Plus 组件名。
 * 支持：PascalCase（直接命中）、`el-icon-xxx`、`xxx`(kebab)。
 * @returns {string|null} 组件名，或 null（无法解析）
 */
export function resolveIcon(raw) {
  if (!raw) return null
  let name = String(raw).trim()
  if (!name) return null
  // 已经是合法的 Element Plus 组件名
  if (EP_NAMES.has(name)) return name
  // 去掉历史前缀
  name = name.replace(/^el-icon-/, '')
  if (RENAME[name]) return RENAME[name]
  const pascal = toPascal(name)
  if (EP_NAMES.has(pascal)) return pascal
  return null
}

export default resolveIcon
