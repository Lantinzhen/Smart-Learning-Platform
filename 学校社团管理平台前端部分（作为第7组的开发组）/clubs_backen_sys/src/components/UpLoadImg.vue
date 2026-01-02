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
    SecretId: 'AKIDi131axHFcdM1GoTQYRRuA1cgArewT3X9Gz4YIefMNXA62a_bHcB9zCH7OmGbRGJQ',
    SecretKey: 'T66EBtQO/A48THhHI581Ys5R6qKA4IcV0uLgmQR/NFo=',
    SecurityToken: 'UhDMm5hQ7zrt8z7Ta2jxwhhHZ8i0b2Bad094a9077b4ea4ea4118ccff3d6a5595K8yxcTNQmrRYLZNJqAk7BIoIwOI47LE3hDo21A77mIDW0IWa9UkoN5k7w29q1ZFB__brsxmDKrmT2FBOYTCUj7nLIaGXF32l-DtAd9sO15a63hSsCrjhxfd6Hv-tju8tl9mrNb2IfO7OW4ByDNHlqjtnbkU7_meZNx_aIgSBdlnpANTVDBCVerCMV03TF9_t6vhOCELCenGkMn-6t2aG-QJ1hdwucuq1duWQw-e8X1ewaNV065O7-SWQamZbW4AIDT83QRsnsgIvX6e7RydHYcCpriHKGNaLTTZBLYbicdkneMT-9pfCz5jIJY9MYBrWJN7mNRutPce_DKX8RjlnbyLrD2G4FbVcsIr_h58D1a1Mi0jNrg1iS7fYnfzpbR9tsFODHtYhf_XN7vb74IlBlGzM5sITjzIN4H0z1zjXO3WN2cx859XAovSxMm0A1auwGPOPn9mI3I7c5Q7RsnHGPqo6IUGQhbHTo6otL1L70qGtClSzaNHe_g6mC0qGHb0dwsGh5BPseMhdlFHf-_cFmxCrg0TEkci96CEvkPx5SMeXA3tT_d1PVgaRNHEdhUYTsTdMozkPzov0LrV4uwjLK73jic1e6QjgAkQC7r4xcIwzjHtpkec4LJNjq6GCTFT1NnOL1Wc0_r13wJnQt0Vouva8tc993VBu1aW-XNK5DWI0MaCExZTBhPe3m3nnC-C9tIRrz9Cq_J51hFw4axqW-VSbTLnWTd7rpufVZM0Vdm82RU2Ax0T3gEitG5CtNa0rQPwdQdP35xei2PGmDyr2yWI4-36Maba7Y0dE1P8kArT73RYWSsBqCkhjd4rlEnMuWhL04DqIKu2Dv62IY5Qy-Upo-QFAq5pS0Q7Pw_u9Ccw'

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