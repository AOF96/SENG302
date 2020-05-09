<template>
    <div class="activityContainer">
        <div id="activityPageTitle" class="activityTitleLabel">{{ activity.name }}</div>
        <div id="activityPageDescription" class="activityDescriptionLabel">{{ activity.description }}</div>
        <div id="activityPageLocation" class="activityLocationLabel">{{ activity.location }}</div>
        <div id="activityPageStartDate" class="activityStartLabel" v-if="activity.continuous === false"><h3> Start date: {{ startDate }}</h3></div>
        <div id="activityPageEndDate" class="activityEndLabel" v-if="activity.continuous === false"><h3> End date: {{ endDate }}</h3></div>
        <div class="activityPageTypeTitle"><p>Activity Type: </p></div>
        <div id="activityPageTypeListing" class="activityTypesContainer">
            <span v-for="a in activity.activityTypes" :key="a.type_id">
                <span v-if="activity.activityTypes.indexOf(a) != activity.activityTypes.length - 1">
                        {{a.name}} ,
                </span>
                <span v-else>
                        {{a.name}}.
                </span>
            </span>
        </div>
        <button class="genericDeleteButton" type="button" v-on:click="deleteActivity(activity)">Delete Activity</button>
    </div>
</template>

<script>

    import dateUtil from "@/util/date";
    import {mapActions, mapGetters} from "vuex";
    import {apiActivity, apiUser} from "../../api";
    import router from "../../router";

  export default {
    name: "ActivityPageInfo",

    computed: {
        ...mapGetters(['activity']),
        ...mapGetters(['user']),

        startDate() {
        return dateUtil.getFormatDate(new Date(this.activity.start_time))
      },

      endDate() {
        return dateUtil.getFormatDate(new Date(this.activity.end_time))
      }
    },
    methods: {
    ...mapActions(['updateUserDurationActivities']),
    ...mapActions(['updateUserContinuousActivities']),

        deleteActivity(activity) {
            apiActivity.deleteActivity(activity.author_id, this.$route.params.activityId)
            .then(
                response => {
                    console.log(response);
                    apiUser
                        .getUserContinuousActivities(this.user.profile_id)
                        .then(response => {
                            this.updateUserContinuousActivities(response.data);
                        });
                    apiUser
                        .getUserDurationActivities(this.user.profile_id)
                        .then(response => {
                            this.updateUserDurationActivities(response.data);
                        });
                    router.push("profile");
                }
            );
        }
    }
  }
</script>

<style scoped>
    @import "../../../public/styles/pages/activityStyle.css";
</style>