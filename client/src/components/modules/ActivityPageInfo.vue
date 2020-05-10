<template>
    <div class="activityContainer">
        <div id="activityPageTitle" class="activityTitle"> {{ activity_name }} </div>
        <div id="activityPageDescription" class="activityDescriptionLabel">{{ description }}</div>
        <div id="activityPageLocation" class="activityLocationLabel">{{ location }}</div>
        <div id="activityPageStartDate" class="activityStartLabel" v-if="continuous === false"><h3> Start date: {{ start_date }}</h3></div>
        <div id="activityPageEndDate" class="activityEndLabel" v-if="continuous === false"><h3> End date: {{ end_date }}</h3></div>
        <div class="activityPageTypeList" id="activityPageTypeListing">
            Activity Type:
            <span v-for="a in activity_types" :key="a.type_id">
                <span v-if="activity_types.indexOf(a) != activity_types.length - 1">
                        {{a.name}},
                </span>
                <span v-else>
                        {{a.name}}.
                </span>
            </span>
        </div>
        <button class="deleteActivityButton" type="button" v-on:click="deleteActivity(user)">Delete Activity</button>
    </div>
</template>

<script>

    import dateUtil from "@/util/date";
    import {mapActions, mapGetters} from "vuex";
    import {apiActivity} from "../../api";
    import router from "../../router";

  export default {
    name: "ActivityPageInfo",
      data() {
        return {
            activity_name: "",
            continuous: false,
            description: "",
            activity_types: [],
            start_date: null,
            end_date: null,
            location: "",
        }
      },

    computed: {
        ...mapGetters(['activity']),
        ...mapGetters(['user']),
    },
      created: function() {
          this.loadActivity();
      },
    methods: {
        ...mapActions(['updateUserDurationActivities']),
        ...mapActions(['updateUserContinuousActivities']),

        deleteActivity(user) {
            apiActivity.deleteActivity(user.profile_id, this.$route.params.activityId);
            this.updateUserDurationActivities(user.dur_activities);
            this.updateUserContinuousActivities(user.cont_activities);
            router.push("/profile");
        },
        async loadActivity() {
            if(this.$route.params.activityId == null || this.$route.params.activityId == ""){
                this.$router.push('/profile');
            }else{
                var tempActivityData = await apiActivity.getActivityById(this.$route.params.activityId);
                if(tempActivityData == "Invalid permissions"){
                    this.$router.push('/profile');
                }else{
                    this.activity_name = tempActivityData.activity_name;
                    this.continuous = tempActivityData.continuous;
                    this.description = tempActivityData.description;
                    this.activity_types = tempActivityData.activity_type;
                    this.start_date = dateUtil.getFormatDate(new Date(tempActivityData.start_time));
                    this.end_date = dateUtil.getFormatDate(new Date(this.activity.end_time));
                    this.location = tempActivityData.location;
                }
            }
        },
    }
  }
</script>

<style scoped>
    @import "../../../public/styles/pages/activityStyle.css";
</style>