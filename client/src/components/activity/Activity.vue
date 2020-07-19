np
<template>
    <div>
        <div class="profileBanner">
        </div>
        <div class="activityWrap">
            <div class="activityWhiteBoxContainer">
                <ActivityPageInfo/>
                <div class="floatClear"></div>
            </div>
        </div>
    </div>
</template>

<script>
  import ActivityPageInfo from "../modules/ActivityPageInfo";
  import {mapGetters} from "vuex";

  export default {
    name: "Activity",
    data: function () {
      return {
        activityData: {
          name: "",
          start: "",
          end: "",
          location: "",
          continuous: true,
          description: "",
          activityTypes: []
        }
      }
    },
    mounted() {
    },
    components: {
      ActivityPageInfo
    },
    computed: {
      ...mapGetters(['activity'])
    },
  }
</script>

<style scoped>
    @import "../../../public/styles/pages/activityStyle.css";
    @import "../../../public/styles/pages/profileStyle.css";
</style>
