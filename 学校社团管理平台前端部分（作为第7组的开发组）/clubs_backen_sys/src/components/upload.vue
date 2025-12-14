<template>
    <div class="upload-container">
        <!-- 文件选择区域 -->
        <div class="upload-area" @click="triggerFileInput" @dragover.prevent="handleDragOver" @drop.prevent="handleDrop"
            :class="{ 'drag-over': isDragOver }">
            <div class="upload-placeholder">
                <UploadIcon class="upload-icon" />
                <p class="upload-text">点击或拖拽上传图片</p>
                <p class="upload-hint">支持 PNG、JPG、GIF 格式，最大 5MB</p>
            </div>
        </div>

        <!-- 隐藏的文件输入 -->
        <input ref="fileInputRef" type="file" accept="image/*" @change="handleFileChange" class="file-input" />

        <!-- 预览区域 -->
        <div v-if="previewUrl" class="preview-container">
            <div class="preview-wrapper">
                <img :src="previewUrl" alt="预览图片" class="preview-image" />
                <button @click="removePreview" class="remove-btn" title="移除图片">
                    <CloseIcon />
                </button>
            </div>
            <div class="preview-info">
                <p>文件名: {{ selectedFile?.name }}</p>
                <p>大小: {{ formatFileSize(selectedFile?.size || 0) }}</p>
            </div>
        </div>

        <!-- 上传按钮 -->
        <button @click="uploadImage" :disabled="!selectedFile || isUploading" class="upload-btn"
            :class="{ 'uploading': isUploading }">
            <span v-if="!isUploading">上传图片</span>
            <span v-else class="uploading-text">
                <LoadingSpinner />
                上传中...
            </span>
        </button>

        <!-- 上传结果 -->
        <div v-if="uploadResult" class="result-container">
            <div v-if="uploadResult.success" class="success-result">
                <SuccessIcon />
                <div class="result-content">
                    <p class="result-title">上传成功！</p>
                    <p class="result-url">
                        URL:
                        <a :href="uploadResult.data.fileUrl" target="_blank" class="url-link">
                            {{ uploadResult.data.fileUrl }}
                        </a>
                    </p>
                    <div class="action-buttons">
                        <button @click="copyUrl" class="action-btn">
                            <CopyIcon />
                            复制链接
                        </button>
                    </div>
                </div>
            </div>
            <div v-else class="error-result">
                <ErrorIcon />
                <div class="result-content">
                    <p class="result-title">上传失败</p>
                    <p class="error-message">{{ uploadResult.message }}</p>
                    <button @click="retryUpload" class="retry-btn">重试</button>
                </div>
            </div>
        </div>

        <!-- 进度条 -->
        <div v-if="isUploading" class="progress-container">
            <div class="progress-bar" :style="{ width: uploadProgress + '%' }"></div>
            <span class="progress-text">{{ Math.round(uploadProgress) }}%</span>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 图标组件（需要根据实际情况替换）
const UploadIcon = { template: '<span>📁</span>' }
const CloseIcon = { template: '<span>✕</span>' }
const LoadingSpinner = { template: '<span class="spinner">⏳</span>' }
const SuccessIcon = { template: '<span>✅</span>' }
const ErrorIcon = { template: '<span>❌</span>' }
const CopyIcon = { template: '<span>📋</span>' }
const ViewIcon = { template: '<span>👁️</span>' }

// 云函数配置
const CLOUD_FUNCTION_URL = 'https://fc-mp-8b70abea-e78e-4c51-a4c6-e3dc5057d1e6.next.bspapp.com/upload/uploadImage'

// 类型定义
interface UploadResult {
    success: boolean
    data?: {
        fileUrl: string
        [key: string]: any
    }
    message?: string
}

interface FileWithPreview extends File {
    preview?: string
}

// 响应式数据
const fileInputRef = ref<HTMLInputElement | null>(null)
const selectedFile = ref<FileWithPreview | null>(null)
const previewUrl = ref<string>('')
const isUploading = ref<boolean>(false)
const uploadProgress = ref<number>(0)
const uploadResult = ref<UploadResult | null>(null)
const isDragOver = ref<boolean>(false)

// 触发文件选择
const triggerFileInput = (): void => {
    fileInputRef.value?.click()
}

// 处理文件选择
const handleFileChange = (event: Event): void => {
    const input = event.target as HTMLInputElement
    const file = input.files?.[0]

    if (file && validateFile(file)) {
        handleSelectedFile(file)
    }
}

// 处理拖拽放置
const handleDrop = (event: DragEvent): void => {
    isDragOver.value = false
    const file = event.dataTransfer?.files[0]

    if (file && validateFile(file)) {
        handleSelectedFile(file)
    }
}

// 处理拖拽悬停
const handleDragOver = (): void => {
    isDragOver.value = true
}

// 处理选中的文件
const handleSelectedFile = (file: File): void => {
    selectedFile.value = file
    uploadResult.value = null

    // 创建预览URL
    const reader = new FileReader()
    reader.onload = (e) => {
        previewUrl.value = e.target?.result as string
    }
    reader.readAsDataURL(file)
}

// 验证文件
const validateFile = (file: File): boolean => {
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
    const maxSize = 5 * 1024 * 1024 // 5MB

    if (!validTypes.includes(file.type)) {
        alert('请选择有效的图片格式（JPG、PNG、GIF、WebP）')
        return false
    }

    if (file.size > maxSize) {
        alert('文件大小不能超过5MB')
        return false
    }

    return true
}

// 文件转Base64
const fileToBase64 = (file: File): Promise<string> => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = () => {
            const base64 = reader.result as string
            // 移除data:image/...;base64,前缀
            const base64Data = base64.split(',')[1]
            resolve(base64Data)
        }
        reader.onerror = (error) => reject(error)
    })
}

// 上传图片到云函数
const uploadImage = async (): Promise<void> => {
    if (!selectedFile.value) return

    isUploading.value = true
    uploadProgress.value = 0
    uploadResult.value = null

    try {
        // 模拟上传进度
        const progressInterval = setInterval(() => {
            if (uploadProgress.value < 90) {
                uploadProgress.value += 10
            }
        }, 200)

        // 转换文件为Base64
        const base64Data = await fileToBase64(selectedFile.value)

        // 准备请求数据
        const requestData = {
            base64: `data:${selectedFile.value.type};base64,${base64Data}`
        }

        // 调用云函数
        const response = await fetch(CLOUD_FUNCTION_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        })

        clearInterval(progressInterval)
        uploadProgress.value = 100

        console.log('云函数响应:', response)
        const result = await response.json()

        if (result.code === 0 || result.success) {
            uploadResult.value = {
                success: true,
                data: {
                    fileUrl: result.fileURL
                }
            }
            console.log('上传成功:', result)
            return result.fileURL
        } else {
            throw new Error(result.message || '上传失败')
        }

    } catch (error: any) {
        console.error('上传错误:', error)
        uploadResult.value = {
            success: false,
            message: error.message || '上传过程中发生错误'
        }
    } finally {
        isUploading.value = false
        uploadProgress.value = 0
    }
}

// 移除预览
const removePreview = (): void => {
    selectedFile.value = null
    previewUrl.value = ''
    uploadResult.value = null

    // 重置文件输入
    if (fileInputRef.value) {
        fileInputRef.value.value = ''
    }
}

// 复制URL到剪贴板
const copyUrl = async (): Promise<void> => {
    if (uploadResult.value?.success && uploadResult.value.data?.fileUrl) {
        try {
            await navigator.clipboard.writeText(uploadResult.value.data.fileUrl)
            alert('链接已复制到剪贴板')
        } catch (error) {
            console.error('复制失败:', error)
            // 降级方案
            const textArea = document.createElement('textarea')
            textArea.value = uploadResult.value.data!.fileUrl
            document.body.appendChild(textArea)
            textArea.select()
            document.execCommand('copy')
            document.body.removeChild(textArea)
            alert('链接已复制到剪贴板')
        }
    }
}

// 查看图片
const viewImage = (): void => {
    if (uploadResult.value?.success && uploadResult.value.data?.fileUrl) {
        window.open(uploadResult.value.data.fileUrl, '_blank')
    }
}

// 重试上传
const retryUpload = (): void => {
    uploadResult.value = null
    uploadImage()
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 组件挂载时设置初始值
onMounted(() => {
    // 可以在这里初始化一些数据
})
</script>

<style scoped>
.upload-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 24px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.upload-area {
    border: 2px dashed #d1d5db;
    border-radius: 12px;
    padding: 48px 24px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    background-color: #f9fafb;
    margin-bottom: 24px;
}

.upload-area:hover {
    border-color: #3b82f6;
    background-color: #eff6ff;
}

.upload-area.drag-over {
    border-color: #3b82f6;
    background-color: #dbeafe;
}

.upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
}

.upload-icon {
    font-size: 48px;
    color: #9ca3af;
}

.upload-text {
    font-size: 18px;
    font-weight: 500;
    color: #374151;
    margin: 0;
}

.upload-hint {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
}

.file-input {
    display: none;
}

.preview-container {
    margin-bottom: 24px;
}

.preview-wrapper {
    position: relative;
    display: inline-block;
    margin-bottom: 12px;
}

.preview-image {
    max-width: 300px;
    max-height: 300px;
    border-radius: 8px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.remove-btn {
    position: absolute;
    top: -8px;
    right: -8px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background-color: #ef4444;
    color: white;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.2s;
}

.remove-btn:hover {
    background-color: #dc2626;
}

.preview-info {
    font-size: 14px;
    color: #6b7280;
}

.preview-info p {
    margin: 4px 0;
}

.upload-btn {
    width: 100%;
    padding: 12px 24px;
    background-color: #3b82f6;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.2s;
}

.upload-btn:hover:not(:disabled) {
    background-color: #2563eb;
}

.upload-btn:disabled {
    background-color: #9ca3af;
    cursor: not-allowed;
}

.upload-btn.uploading {
    background-color: #6b7280;
}

.uploading-text {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}

.spinner {
    animation: spin 1s linear infinite;
}

@keyframes spin {
    from {
        transform: rotate(0deg);
    }

    to {
        transform: rotate(360deg);
    }
}

.result-container {
    margin-top: 24px;
    padding: 16px;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
}

.success-result {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    color: #059669;
}

.error-result {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    color: #dc2626;
}

.result-content {
    flex: 1;
}

.result-title {
    font-weight: 600;
    margin: 0 0 8px 0;
    font-size: 16px;
}

.result-url {
    word-break: break-all;
    font-size: 14px;
    margin: 8px 0;
}

.url-link {
    color: #3b82f6;
    text-decoration: none;
}

.url-link:hover {
    text-decoration: underline;
}

.action-buttons {
    display: flex;
    gap: 12px;
    margin-top: 12px;
}

.action-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background-color: #f3f4f6;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.2s;
}

.action-btn:hover {
    background-color: #e5e7eb;
}

.error-message {
    font-size: 14px;
    margin: 8px 0;
    color: #6b7280;
}

.retry-btn {
    margin-top: 12px;
    padding: 8px 16px;
    background-color: #ef4444;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
}

.retry-btn:hover {
    background-color: #dc2626;
}

.progress-container {
    margin-top: 16px;
    height: 4px;
    background-color: #e5e7eb;
    border-radius: 2px;
    overflow: hidden;
    position: relative;
}

.progress-bar {
    height: 100%;
    background-color: #3b82f6;
    transition: width 0.3s ease;
}

.progress-text {
    position: absolute;
    right: 0;
    top: -20px;
    font-size: 12px;
    color: #6b7280;
}
</style>