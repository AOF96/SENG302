<template>
    <div>
        <div class="profileBanner">
        </div>
        <div class="activityWrap">
            <ActivityPageInfo/>
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
