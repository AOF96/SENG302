np
<template>
    <div>
        <NavBar/>
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
  import NavBar from "../modules/NavBar";
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
      NavBar,
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
    @import "../../../public/styles/main.css";
</style>