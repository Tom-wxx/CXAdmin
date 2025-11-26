<template>
  <div class="file-management-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-files stat-icon" style="color: #409EFF"></i>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalFiles || 0 }}</div>
              <div class="stat-label">文件总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-picture stat-icon" style="color: #67C23A"></i>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.imageCount || 0 }}</div>
              <div class="stat-label">图片文件</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-document stat-icon" style="color: #E6A23C"></i>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.documentCount || 0 }}</div>
              <div class="stat-label">文档文件</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-coin stat-icon" style="color: #F56C6C"></i>
            <div class="stat-info">
              <div class="stat-value">{{ formatFileSize(statistics.totalSize) }}</div>
              <div class="stat-label">存储空间</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主内容区 -->
    <el-card class="main-card">
      <!-- 搜索栏 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="文件名称" prop="fileName">
          <el-input
            v-model="queryParams.fileName"
            placeholder="请输入文件名称"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="文件类型" prop="category">
          <el-select v-model="queryParams.category" placeholder="请选择文件类型" clearable size="small">
            <el-option label="全部" value=""></el-option>
            <el-option label="图片" value="image"></el-option>
            <el-option label="文档" value="document"></el-option>
            <el-option label="视频" value="video"></el-option>
            <el-option label="音频" value="audio"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" class="toolbar">
        <el-col :span="1.5">
          <el-button
            type="primary"
            icon="el-icon-upload"
            size="small"
            @click="handleUpload"
          >上传文件</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            icon="el-icon-delete"
            size="small"
            :disabled="multiple"
            @click="handleDelete"
          >删除</el-button>
        </el-col>
      </el-row>

      <!-- 文件列表 -->
      <el-table
        v-loading="loading"
        :data="fileList"
        @selection-change="handleSelectionChange"
        style="margin-top: 20px"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="文件预览" align="center" width="100">
          <template slot-scope="scope">
            <div class="file-preview" @click="handlePreview(scope.row)">
              <img
                v-if="scope.row.category === 'image'"
                :src="getFileUrl(scope.row.fileUrl)"
                class="preview-image"
              />
              <i v-else :class="getFileIcon(scope.row.category)" class="preview-icon"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="文件名称" prop="originalName" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span class="file-name" @click="handlePreview(scope.row)">{{ scope.row.originalName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="文件类型" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="getCategoryType(scope.row.category)" size="small">
              {{ getCategoryLabel(scope.row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" align="center" width="100">
          <template slot-scope="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="下载次数" align="center" prop="downloadCount" width="100" />
        <el-table-column label="上传时间" align="center" prop="createTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handlePreview(scope.row)"
            >预览</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-download"
              @click="handleDownload(scope.row)"
            >下载</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              style="color: #F56C6C"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog title="上传文件" :visible.sync="uploadDialogVisible" width="600px" @close="handleUploadClose">
      <el-upload
        ref="upload"
        class="upload-container"
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :file-list="uploadFileList"
        multiple
        :auto-upload="false"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          <div>支持图片、文档、视频、音频等多种格式</div>
          <div>单个文件大小不超过 100MB</div>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitUpload">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      :title="previewFile.originalName"
      :visible.sync="previewDialogVisible"
      width="800px"
      @close="handlePreviewClose"
    >
      <div class="preview-content">
        <!-- 图片预览 -->
        <img
          v-if="previewFile.category === 'image'"
          :src="getFileUrl(previewFile.fileUrl)"
          class="preview-full-image"
        />
        <!-- 视频预览 -->
        <video
          v-else-if="previewFile.category === 'video'"
          :src="getFileUrl(previewFile.fileUrl)"
          controls
          class="preview-video"
        ></video>
        <!-- 音频预览 -->
        <audio
          v-else-if="previewFile.category === 'audio'"
          :src="getFileUrl(previewFile.fileUrl)"
          controls
          class="preview-audio"
        ></audio>
        <!-- 其他文件显示信息 -->
        <div v-else class="preview-info">
          <i :class="getFileIcon(previewFile.category)" class="preview-large-icon"></i>
          <div class="file-details">
            <p><strong>文件名：</strong>{{ previewFile.originalName }}</p>
            <p><strong>文件大小：</strong>{{ formatFileSize(previewFile.fileSize) }}</p>
            <p><strong>文件类型：</strong>{{ previewFile.fileType }}</p>
            <p><strong>上传时间：</strong>{{ parseTime(previewFile.createTime) }}</p>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleDownload(previewFile)">下载文件</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFile, deleteFile, getFileStatistics, getFileUrl, downloadFile } from '@/api/system/file'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination'

export default {
  name: 'FileManagement',
  components: {
    Pagination
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 文件列表
      fileList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fileName: undefined,
        category: undefined
      },
      // 统计数据
      statistics: {
        totalFiles: 0,
        totalSize: 0,
        imageCount: 0,
        documentCount: 0
      },
      // 上传对话框
      uploadDialogVisible: false,
      uploadUrl: process.env.VUE_APP_BASE_API + '/system/file/upload',
      uploadHeaders: { Authorization: 'Bearer ' + getToken() },
      uploadFileList: [],
      // 预览对话框
      previewDialogVisible: false,
      previewFile: {}
    }
  },
  created() {
    this.getList()
    this.getStatistics()
  },
  methods: {
    /** 查询文件列表 */
    getList() {
      this.loading = true
      listFile(this.queryParams).then(response => {
        this.fileList = response.data.records
        this.total = response.data.total
        this.loading = false
      })
    },
    /** 获取统计数据 */
    getStatistics() {
      getFileStatistics().then(response => {
        this.statistics = response.data || {}
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        fileName: undefined,
        category: undefined
      }
      this.handleQuery()
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.fileId)
      this.multiple = !selection.length
    },
    /** 上传文件 */
    handleUpload() {
      this.uploadDialogVisible = true
      this.uploadFileList = []
    },
    /** 提交上传 */
    submitUpload() {
      this.$refs.upload.submit()
    },
    /** 上传前校验 */
    beforeUpload(file) {
      const maxSize = 100 * 1024 * 1024 // 100MB
      if (file.size > maxSize) {
        this.$message.error('文件大小不能超过 100MB')
        return false
      }
      return true
    },
    /** 上传成功 */
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.$message.success('上传成功')
        this.getList()
        this.getStatistics()
      } else {
        this.$message.error(response.message || '上传失败')
      }
      // 如果所有文件都上传完成，关闭对话框
      const allUploaded = fileList.every(f => f.status === 'success' || f.status === 'fail')
      if (allUploaded) {
        setTimeout(() => {
          this.uploadDialogVisible = false
        }, 500)
      }
    },
    /** 上传失败 */
    handleUploadError(err, file, fileList) {
      this.$message.error('上传失败: ' + err.message)
    },
    /** 上传对话框关闭 */
    handleUploadClose() {
      this.$refs.upload.clearFiles()
    },
    /** 预览文件 */
    handlePreview(row) {
      this.previewFile = row
      this.previewDialogVisible = true
    },
    /** 预览对话框关闭 */
    handlePreviewClose() {
      this.previewFile = {}
    },
    /** 下载文件 */
    handleDownload(row) {
      const fileId = row.fileId
      downloadFile(fileId).then(response => {
        const blob = new Blob([response])
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = row.originalName
        link.click()
        window.URL.revokeObjectURL(url)
        this.$message.success('下载成功')
      }).catch(() => {
        this.$message.error('下载失败')
      })
    },
    /** 删除文件 */
    handleDelete(row) {
      const fileIds = row.fileId ? [row.fileId] : this.ids
      this.$confirm('是否确认删除选中的文件?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return deleteFile(fileIds.join(','))
      }).then(() => {
        this.getList()
        this.getStatistics()
        this.$message.success('删除成功')
      }).catch(() => {})
    },
    /** 获取文件URL */
    getFileUrl(fileUrl) {
      return getFileUrl(fileUrl)
    },
    /** 格式化文件大小 */
    formatFileSize(bytes) {
      if (!bytes || bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
    },
    /** 获取文件图标 */
    getFileIcon(category) {
      const icons = {
        image: 'el-icon-picture',
        document: 'el-icon-document',
        video: 'el-icon-video-camera',
        audio: 'el-icon-headset',
        other: 'el-icon-document'
      }
      return icons[category] || 'el-icon-document'
    },
    /** 获取分类标签 */
    getCategoryLabel(category) {
      const labels = {
        image: '图片',
        document: '文档',
        video: '视频',
        audio: '音频',
        other: '其他'
      }
      return labels[category] || '其他'
    },
    /** 获取分类标签类型 */
    getCategoryType(category) {
      const types = {
        image: 'success',
        document: 'warning',
        video: 'danger',
        audio: 'info',
        other: ''
      }
      return types[category] || ''
    },
    /** 格式化时间 */
    parseTime(time) {
      if (!time) return ''
      return time.replace('T', ' ')
    }
  }
}
</script>

<style lang="scss" scoped>
.file-management-container {
  padding: 20px;

  .statistics-row {
    margin-bottom: 20px;

    .stat-card {
      ::v-deep .el-card__body {
        padding: 20px;
      }

      .stat-content {
        display: flex;
        align-items: center;
        gap: 20px;

        .stat-icon {
          font-size: 48px;
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #303133;
            margin-bottom: 5px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .main-card {
    .search-form {
      margin-bottom: 10px;
    }

    .toolbar {
      margin-bottom: 10px;
    }

    .file-preview {
      width: 60px;
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      border-radius: 4px;
      overflow: hidden;
      background: #f5f7fa;

      &:hover {
        transform: scale(1.1);
        transition: transform 0.3s;
      }

      .preview-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .preview-icon {
        font-size: 32px;
        color: #909399;
      }
    }

    .file-name {
      color: #409EFF;
      cursor: pointer;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .upload-container {
    ::v-deep .el-upload-dragger {
      width: 100%;
      height: 200px;
    }
  }

  .preview-content {
    text-align: center;

    .preview-full-image {
      max-width: 100%;
      max-height: 500px;
    }

    .preview-video {
      width: 100%;
      max-height: 500px;
    }

    .preview-audio {
      width: 100%;
    }

    .preview-info {
      padding: 40px;

      .preview-large-icon {
        font-size: 120px;
        color: #909399;
        display: block;
        margin-bottom: 30px;
      }

      .file-details {
        text-align: left;
        background: #f5f7fa;
        padding: 20px;
        border-radius: 8px;

        p {
          margin: 10px 0;
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }
}
</style>
