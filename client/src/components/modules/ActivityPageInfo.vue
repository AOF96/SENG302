<template>
    <div class="activityContainer">
        <h3 id="activityPageTitle" class="activityTitle"> {{ activity_name }} </h3>
        <hr class="activityPageActivitySeparator">
        <div id="activityPageDescription" class="activityDescriptionLabel">{{ description }}</div>
        <div id="activityPageLocation" class="activityLocationLabel">{{ location }}</div>
        <div id="activityPageStartDate" class="activityStartLabel" v-if="continuous === false && loaded === true"><h3>
            Start date: {{ start_date }}</h3></div>
        <div id="activityPageEndDate" class="activityEndLabel" v-if="continuous === false && loaded === true"><h3> End
            date: {{ end_date }}</h3></div>
        <div class="activityPageTypeList" id="activityPageTypeListing" v-if="loaded === true">
            Activity Type:
            <span v-for="a in activity_types" :key="a.type_id">
                <span v-if="activity_types.indexOf(a) !== activity_types.length - 1">
                        {{a.name}},
                </span>
                <span v-else>
                        {{a.name}}.
                </span>
            </span>
        </div>
        <div id="activityAuthor" class="activityAuthorLabel" v-if="loaded === true">
            <h3> Created by: {{activity_author_firstname + " " + activity_author_lastname }}</h3>
        </div>
        <div class="activityPageBottomButtons">
            <router-link v-bind:to="'/profile/'+authorId">
                <button class="genericConfirmButton activityPageBackToProfileButton activityPageBackToProfileButtonSpacing">
                    Back to Profile
                </button>
            </router-link>
            <router-link v-if="authorId===user.profile_id || user.permission_level > 0" v-bind:to="'/activity_editing/' + activityId">
                <button
                        class="genericConfirmButton activityPageEditActivityButton activityPageEditActivityButtonSpacing"
                        type="button"
                >Edit Activity
                </button>
            </router-link>
            <button v-if="authorId===user.profile_id || user.permission_level > 0"
                    class="genericDeleteButton activityPageDeleteActivityButton activityPageDeleteActivityButtonSpacing"
                    type="button" v-on:click="deleteCurrentActivity()">Delete Activity
            </button>
        </div>
    </div>
</template>

<script>

    import dateUtil from "@/util/date";
    import {mapActions, mapGetters} from "vuex";

    export default {
        name: "ActivityPageInfo",
        data() {
            return {
                activity_name: "",
                activity_author_firstname: "",
                activity_author_lastname: "",
                continuous: false,
                description: "",
                activity_types: [],
                start_date: null,
                end_date: null,
                location: "",
                loaded: false,
                authorId: null,
                activityId: null
            }
        },

        computed: {
            ...mapGetters(['activity']),
            ...mapGetters(['user']),
        },
        created: function () {
            this.loadActivity();
        },
        methods: {
            ...mapActions(['updateUserDurationActivities', "getUserContinuousActivities", "getUserDurationActivities", "deleteActivity", "getActivityById", 'updateUserContinuousActivities']),
            /**
             * Deletes the current activity
             */
            deleteCurrentActivity() {
                this
                    .deleteActivity({'userId': this.user.profile_id, 'activityId':this.$route.params.activityId})
                    .then(response => {
                        console.log(response);
                        this
                            .getUserContinuousActivities(this.user.profile_id)
                            .then(response => {
                                this.updateUserContinuousActivities(response.data);
                            });
                        this
                            .getUserDurationActivities(this.user.profile_id)
                            .then(response => {
                                this.updateUserDurationActivities(response.data);
                            });
                        this.$router.push("/profile/" + this.authorId);
                    });
            },
            /**
             * Requests the activity and loads its information
             * @returns {Promise<void>}
             */
            async loadActivity() {
                if (this.$route.params.activityId == null || this.$route.params.activityId === "") {
                    this.$router.push('/profile');
                } else {
                    var tempActivityData = await this.getActivityById(this.$route.params.activityId);
                    console.log(tempActivityData);
                    if (tempActivityData === "Invalid permissions") {
                        this.$router.push('/profile');
                    } else {
                        this.activityId = tempActivityData.id;
                        this.activity_name = tempActivityData.activity_name;
                        this.continuous = tempActivityData.continuous;
                        this.description = tempActivityData.description;
                        this.activity_types = tempActivityData.activity_type;
                        this.start_date = dateUtil.getFormatDate(new Date(tempActivityData.start_time));
                        this.end_date = dateUtil.getFormatDate(new Date(tempActivityData.end_time));
                        this.location = tempActivityData.location;
                        this.activity_author_firstname = tempActivityData.author.firstname;
                        this.activity_author_lastname = tempActivityData.author.lastname;
                        this.authorId = tempActivityData.author.profile_id;
                        this.loaded = true;
                    }
                }
            },
        }
    }
</script>

<style scoped>
    @import "../../../public/styles/pages/activityStyle.css";
</style>
