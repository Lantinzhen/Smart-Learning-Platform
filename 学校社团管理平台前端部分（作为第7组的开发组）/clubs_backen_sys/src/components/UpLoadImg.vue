<template>
  <div class="file-upload-container">
    <!-- 上传图片区域 -->
    <div class="file-selector-container"
         @click="openFileSelector">
      <input ref="uploadImgFile"
             type="file"
             style="display: none;"
             @change="handleFileSelect" />

      <img v-if="img_file"
           :src="img_file"
           alt=""
           class="img-show">
      <span v-else>点击上传图片</span>
    </div>

  </div>
</template>
<script setup>
import { ref, watch, onMounted } from 'vue'
import COS from 'cos-js-sdk-v5';


const uploadImgFile = ref(null);
const img_file = ref("")
const selectedFile = ref(null); // 使用 ref 存储文件 

const props = defineProps({
  // 海报名称
  fileName: {
    type: String,
    default: 'fileNameDefault'
  },
})
const emit = defineEmits(['receivedUrl']);

// 触发文件选择
const openFileSelector = () => {
  if (uploadImgFile.value) {
    uploadImgFile.value.click();
  }
}

// 文件选择完成后，触发上传
const handleFileSelect = (e) => {
  const files = e.target.files;
  if (!files.length) return;
  selectedFile.value = files[0];
  img_file.value = URL.createObjectURL(selectedFile.value);

  // 上传
  uploadToCos();
};


const uploadToCos = () => {

  // 存储桶名称，由bucketname-appid 组成，appid必须填入，可以在COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket
  const Bucket = 'pqnlin-1391348613';
  // 存储桶Region可以在COS控制台指定存储桶的概览页查看 https://console.cloud.tencent.com/cos5/bucket/ 
  // 关于地域的详情见 https://cloud.tencent.com/document/product/436/6224
  const Region = 'ap-guangzhou';

  // 初始化实例，详情参考：https://cloud.tencent.com/document/product/436/11459

  // 1、方式一：传入临时密钥
  const cos = new COS({
    SecretId: 'AKIDsx17NJWoDOeSwwCIKAsBumc-NLSgO3d322eXPNtmC4kGwaOcJvJVtxOCiND5gtwB',
    SecretKey: 'rBuH3vLnEya5qkbwqUSRUGvvti1gWlRgrhEUT/PcE0s=',
    SecurityToken: '9wmpNK5hfJ49HQRRiKlUtDod6wW3xGva6c2b9d9ee12eb591d31ee765b9c67b6c3AiJAp9PwaYvXBtEzHpUPaCUp0vfMqly8pL0P70hy6XtJntCPFfhm89fQzpjEOP8ea_S6qTduJHqX8zv6Mu_tvUNR63QqlWIWNcUQ04qgCdBwiTh3RYco9iKOh1I-5HnH0foqtetM9jfNOwnde8H8PYpvJLSapByYpFRv4_pxJpb2T8uLeWZt8jujoIWfax-LQ1sKNl5CLOIuvbSzSudLbKxWn1r6GhL6emn-syH6qoipNXNqXw3orqp3f8qthlokY7Hz2qhypwjTa95DOLrwMiPAiCT-fLWdqbFitikuW-R6gINcVGdfOKLlV929WMlAB9WCsMxn424-r9-NY8X_qqP7ombBRbtApzCgk2KcdsabMJLofAKowXB0KmxUtu5LgglLKo7Cv82CNJwdvJxFGnDFalUQF1I6w2kADv-8aQqIQn361g84j15TB9-mrcP6aFAigLo90GkiTIOFu8YEDSSmlCDXQwCkVfLKcKEeKABH4nXp4z-dSESF4KijXdSXVkXLdY8rjhBlhC6QLejBogKtOewqRuFhm7R17oparqYgz-dDk7C84wK9pf4J-Lf6jQhZg2Y2adRoNoFS_aEjqIzQ9PWHbhbQ-xR0C3R_2iCXhBnjH5FWpxcE60Ff6DRXYTlRbx603dCodfzRqUqk-Dxcn64hH4l4J04u1_pwcnb2P0W5eqjlgo9ZKVSBGGCepyD5_IjPa-LKU3ore2UUIYIkN8A153VcNlO4Sp7jiFBxW-aA7eob8P2qLQHpbur4de9sK8s55KV5IK4FCsNOLgjTP9HLkNbVKJ5try4f-IBsd1lTXzDV2leK-xoFbpg214MZmYS1g3yq-RO9RB29_SXnhkTTlxZPtWX8Si5o98'

  })
  let taskId;

  const fileExtension = selectedFile.value.name.split('.').pop();
  const actualFileName = props.fileName || `upload_${Date.now()}`;
  // 上传文件
  cos.uploadFile({
    Bucket: Bucket,
    Region: Region,
    Key: "学生社团管理/" + actualFileName + "." + fileExtension,
    Body: selectedFile.value,
    SliceSize: 1024 * 1024, // 大于1mb才进行分块上传
    onTaskReady: function (tid) {
      taskId = tid;
    },
    onProgress: function (progressData) {
      console.log('上传中', JSON.stringify(progressData));
    },
  }, function (err, data) {
    console.log(err, data);
    if (err) {
      console.error('上传失败:', err);
    } else {
      console.log('上传成功:', data);
      // 使用 emit 将 url 发送给父组件
      emit('receivedUrl', 'https://' + data.Location);

    }
  });
  // 可使用队列暂停、重启任务
  // cos.pauseTask(taskId);


};




</script>
<style scoped>
.file-upload-container {
  width: 100%;
  font-size: 20px;
  color: #919395;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.file-selector-container {
  width: 100px;
  height: 100px;
  padding: 2px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  transition: all 0.1s linear;
  margin-bottom: 5px;
}
.file-selector-container:hover {
  border-color: #409eff;
}
.file-selector-container:active {
  transform: scale(1.05);
}
.file-selector-container span {
  font-size: 14px;
}
.fileSelect {
  opacity: 0;
  width: 100%;
  height: 100%;
}
.img-show {
  max-width: 100%;
  max-height: 100%;
}
</style>