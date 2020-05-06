np<template>
    <div>
        <NavBar/>
        <div class="profileBanner">
        </div>
        <div class="activityContainer">
            <div class="profilePublicInfo">
                    <ActivityPageInfo/>
                <div class="floatClear"></div>
            </div>
        </div>
    </div>
</template>

<script>

  import NavBar from '@/components/NavBar';
  import ActivityPageInfo from "@/components/modules/ActivityPageInfo";
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

<style>
    @import "../../public/styles/pages/activities.css";
</style>