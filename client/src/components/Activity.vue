np<template>
    <div>
        <NavBar/>
        <div id="profileBanner">
        </div>
        <div id="activityWrap">
            <div id="profilePublicInfo">
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

<style scoped>
    #activityWrap{
        width: 60%;
        margin: 0 auto;
        display: block;
        position: relative;
        top: -56px;
    }
</style>
