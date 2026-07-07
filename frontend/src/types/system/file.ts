import type { BaseEntity } from '@/types/base'
import type { PageQuery } from '@/types/api'

/** 文件信息对象（对应后端 sys_file / SysFile） */
export interface FileInfo extends BaseEntity {
  fileId?: number
  fileName?: string
  originalName?: string
  filePath?: string
  fileUrl?: string
  fileSize?: number
  fileType?: string
  fileExt?: string
  /** 文件分类 image/document/video/audio/other */
  category?: string
  storageType?: string
  md5?: string
  downloadCount?: number
  status?: string
}

/** 文件列表查询参数 */
export interface FileQuery extends PageQuery {
  fileName?: string
  category?: string
  fileType?: string
  status?: string
}

/** 文件统计信息（字段以后端实际返回为准，组件转换时再细化） */
export interface FileStatistics {
  totalCount?: number
  totalSize?: number
  [key: string]: number | undefined
}
