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
    SecretId: 'AKIDtb7J03cZPEql2C6N8_JZ-R_R65wTCoZ_Awi4guU7Tk8pI3e_m6irr_P4zixpprKg',
    SecretKey: 'm7oQHim4ueOvI7Abdu8dU7E7Hj11ARjNrpwUvbtUq8M=',
    SecurityToken: 'DMca4VGr2saR5iRom1uksbzbW4jiyfya25e1aa7b33f69bb6dce8e5d3bab9745chqE_sqob-6O6dqmVJRZOT1Lnd8OMortncq3dn-6o9Zq2rHbikOCfrvKPi3OSoXUoKyTWBb4Pn4DbeS5yS5aTN18aoW8hhh8d-1DuBuJxZrPG8M3W5wsni0SKMB25Ktkbl10rIpPhIQw8jO83R2Z1tjT2LGv5xNyOv8s71Q_-k5tmxpBT0IakzEzrTbLhksPqVl8Ne36Hek3xCj5oFjjyjUIkkeZEZ2ASPksBs3Jb_og4m68WmLQRgucgsbxWkKcyp64j5veped8aMIYsk2Bvi8zg6LyWZokwOwqn7IkeVXTNHggQwovPBIWTnFX8FGJinxDXoNMkkP5zubR2CA7vmvIIHzkWxUzPDSyacxbv0QoQ2aEgHfrz-HBr0oFCGHSiM0WeY1ugERE03z7pyMruzCpyOjA_xS6SFKNOc0QBgPHOd7Y8W_fW1T2yWq3CiNlG0KFszUEkZLK-IZQkGfccWGoK6aEJe7dUwrPOkmFivrDxbSM4d66WGxFwjZH8P5sO-g5UorBIc78n4itwFMJD6iIvfEhZzv_GMvrkB8QSEbtZbtkQElzKujKa-TWxr5YS-qkUF4MKvdv2aD9uVo7fc_x3vHaRulwGKA1IbbhRDK3qUHHc9ZtiL_qP8DF-QKnMOki5aaYAgpEpLgbz7C4RFfAHjYMikma4d9P0e6TveIDbHn5TiZaKRVla6oJ6EBnTmNJiMlR5vMb_Pp8o2CfwWa_WDZl1S9DeM_hLS9BrC5LNJCqNEDSBJmgZdwUT8rlplBHD_5cA8Jdp1gYnzTMnNw8NNyquqa9pLR06Uw1X-WuVDvqrPwEVm9k02XT0ddW66sPzxYReDzdRk-JTLZ6G5O06NPK9ePe7TnFBGW81PN0'

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