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
  import {apiActivity} from "@/api";

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
    beforeRouteEnter(to, from, next) {
        const activityId = to.params.activityId;
        apiActivity.getActivity(activityId).then(resp => {
            console.log(resp.data)
        })
        console.log(from)
        console.log(next)
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
