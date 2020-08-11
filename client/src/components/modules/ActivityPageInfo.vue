<template>
  <v-container fluid grid-list-md fill-height fill-width>
    <v-layout row wrap width="600px">
      <v-flex d-flex xs12 sm6 md4>
        <v-card class="activityContainer"
                :loading="loadingActivity"
                width="1200px"
                style="border-radius: 14px; margin-left: -60px"
        >
          <h3 id="activityPageTitle" class="activityTitle"> {{ activity_name }} </h3>
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
                  type="button" id="activityPageInfoDeleteButton" v-on:click="deleteActivity()">Delete Activity
          </button>
          <div v-if="!userFollowing">
            <v-btn v-on:click="followCurrentActivity()" color="#1cca92" outlined rounded large>Follow</v-btn>
          </div>
          <div v-else>
            <v-btn v-on:click="unFollowCurrentActivity()" color="#f06a6a" outlined rounded large>Un follow</v-btn>
          </div>
        </div>
      </v-card>
      </v-flex>

      <v-flex d-flex xs12 sm3 md3>
        <v-layout row wrap>
          <v-flex>
            <v-card height="300px">
              <h2>Organisers/Participants</h2>
              <h3>Coming soon!</h3>
            </v-card>
          </v-flex>

          <v-flex>
            <v-card height="300px">
              <h2>Map track</h2>
              <h3>Coming next sprint!</h3>
            </v-card>
          </v-flex>

        </v-layout>
      </v-flex>

      <v-flex d-flex xs12 sm3 md3>
        <v-layout row wrap>
          <v-flex>
            <v-card height="300px">
              <h2>Gallery</h2>
              <h3>Coming at some stage!</h3>
            </v-card>
          </v-flex>

          <v-flex>
            <v-card height="300px">
              <h2>Updates</h2>
              <h3>Coming next month!</h3>
            </v-card>
          </v-flex>

        </v-layout>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>

  import dateUtil from "@/util/date";
  import {mapActions, mapGetters} from "vuex";
  import {apiActivity, apiUser} from "../../api";

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
        activityId: null,
        loadingActivity: true,
        userFollowing: null,
      }
    },

    computed: {
      ...mapGetters(['activity']),
      ...mapGetters(['user']),
    },
    created: function () {
      this.loadActivity();
      return this.checkFollowing();
    },
    methods: {
      ...mapActions(['updateUserDurationActivities', 'updateUserContinuousActivities', "addActivityAchievement",
        "editActivityAchievement", "deleteActivityAchievement"]),

      /**
       * Deletes the current activity
       */
      async deleteActivity() {
        try {
          let response = await apiActivity.deleteActivity(this.user.profile_id, this.$route.params.activityId)
          response = await apiUser.getUserContinuousActivities(this.user.profile_id)
          this.updateUserContinuousActivities(response.data);
          response = await apiUser.getUserDurationActivities(this.user.profile_id)
          this.updateUserDurationActivities(response.data);
          this.$router.push("/profile/" + this.authorId);
        } catch (err) {
          console.error(err)
        }
      },
      /**
       * Requests the activity and loads its information
       * @returns {Promise<void>}
       */
      async loadActivity() {
        if (this.$route.params.activityId == null || this.$route.params.activityId === "") {
          this.$router.push('/profile');
        } else {
          var tempActivityData = await apiActivity.getActivityById(this.$route.params.activityId);
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
            this.loadingActivity = false;
          }
        }
      },
        /**
         * Checks if user is following current activity and sets userFollowing which is used to determine if
         * the follow button should be for following or unfollowing
         * @returns {Promise<void>}
         */
        async checkFollowing() {
            await apiUser.isUserFollowingActivitiy(this.user.profile_id, this.$route.params.activityId)
                .then((response) => {
                  this.userFollowing = response.data !== false;
                })
                .catch((error) => {
                    if (error.response.status === 404) {
                        this.userFollowing = false;
                    }
                });
        },
        /**
         * Makes api call to allow a user to follow current activity after follow button is pressed
         * @returns {Promise<void>}
         */
        async followCurrentActivity() {
            await apiActivity.followActivity(this.user.profile_id,  this.$route.params.activityId).then(response => {
                if (response.status === 201) {
                    this.userFollowing = true;
                }
            });
        },

        /**
         * Makes api call to allow a user to un follow current activity after un follow button is pressed
         * @returns {Promise<void>}
         */
        async unFollowCurrentActivity() {
            await apiActivity.unfollowActivity(this.user.profile_id,  this.$route.params.activityId).then(response => {
            if (response.status === 200) {
                this.userFollowing = false;
            }
            });
        }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/activityStyle.css";
</style>
