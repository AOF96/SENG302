np<template>
    <div>
        <NavBar/>
        <div class="profileBanner">
        </div>
        <div class="activityContainer">
            <div class="profilePublicInfo">
                    <ActivityPageInfo :activityInfo="activityData"/>
                <div class="floatClear"></div>
            </div>
        </div>
    </div>
</template>

<script>

  import NavBar from '@/components/NavBar';
  import ActivityPageInfo from "@/components/modules/ActivityPageInfo";
  import {mapGetters} from "vuex";
  import {apiActivity} from "@/api";

  export default {
    name: "Activity",
    data: function () {
      return {
        activityData: {
          name: "NZ AUS Trail Running Walking",
          start: "25th April 2020",
          end: "26th April 2020",
          location: "Kaikoura, New Zealand",
          continuous: true,
          description: "Trail running is a popular sport which involves running trails through challenging terrain. Trail running is a popular sport which involves running trails through challenging terrain.Trail running is a popular sport which involves running trails through challenging terrain.Trail running is a popular sport which involves running trails through challenging terrain. Trail running is a popular sport which involves running trails through challenging terrain.",
          activityTypes: [
            {
              "type_id": 6,
              "name": "Hiking"
            },
            {
              "type_id": 7,
              "name": "Running"
            },
          ]

        }
      }
    },
    mounted() {
      apiActivity.getActivity(this.$route.params.activityId)
        .then(
          response => {
            this.activityData.name = response.data.activity_name;
            this.activityData.description = response.data.description;
            this.activityData.activityTypes = response.data.activity_type;
            this.activityData.start = response.data.start_time;
            this.activityData.end = response.data.end_time;
            this.activityData.location = response.data.location;
            this.activityData.continuous = response.data.continuous;

          }
        ).catch(err => {
          console.log(err);
        }
      )
    },
    components: {
      NavBar,
      ActivityPageInfo
    },
    computed: {
      ...mapGetters(['user'])
    },
  }
</script>

<style>
    @import "../../public/styles/pages/activities.css";
</style>