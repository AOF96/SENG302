<template>
  <div>
    <div class="profileBanner"></div>
    <v-snackbar v-model="snackbar">{{ errorMessage }}<v-btn color="primary" text @click="snackbar = false" >Close</v-btn></v-snackbar>
    <div class="activityWrap">
      <div id="activityPageLeft" class="activityPageColumn">
        <v-card style="border-radius: 15px" class="activityContainer" :loading="loadingActivity">
          <div id="activityPageVisibility" class="activityVisibilityLabel"
               v-bind:class="{activityVisibilityLabel:true,
               activityVisibilityLabelRed: visibility === 'private',
               activityVisibilityLabelOrange: visibility === 'restricted',
               activityVisibilityLabelGreen: visibility === 'public'}">{{visibility}}</div>
          <div id="activityPageLocation" class="activityLocationLabel">{{ locationFormat(location) }}</div>
          <h3 id="activityPageTitle" class="activityTitle"> {{ activity_name }} </h3>
          <div id="activityPageDescription" class="activityDescriptionLabel">{{ description }}</div>
          <h3 id="activityPageStartDate" class="activityStartLabel" v-if="continuous === false && loaded === true"><b>Starts:</b> {{ start_date }}</h3>
          <h3 id="activityPageEndDate" class="activityEndLabel" v-if="continuous === false && loaded === true"><b>Ends:</b> {{ end_date }}</h3>
          <div class="activityPageTypeList" id="activityPageTypeListing" v-if="loaded === true">
            <span v-for="(a, index) in activity_types" :key="index">
              <v-chip
                      class="ma-2"
              >
                {{a.name}}
              </v-chip>
            </span>
          </div>
          <v-row no-gutters id="activityAuthor" class="activityAuthorLabel" v-if="loaded === true">
            <v-chip
                    v-bind:to="'/profile/'+authorId"
                    class="ma-2"
                    color="white"
            >
              <v-avatar left>
                <v-icon>mdi-account-circle</v-icon>
              </v-avatar>
              {{activity_author_firstname + " " + activity_author_lastname }}
            </v-chip>
            <v-spacer></v-spacer>
            <v-chip
                    class="ma-2"
                    color="white"
            >
              {{numFollowers}} Follower<span v-if="numFollowers != 1">s</span>
            </v-chip>
          </v-row>
          <v-divider></v-divider>
          <v-row no-gutters justify="center" class="activityPageBottomButtons">
            <v-btn style="margin: 5px" v-if="authorId===user.profile_id || user.permission_level > 0" v-bind:to="'/activity_editing/' + activityId" color="blue" outlined rounded>Edit</v-btn>
            <v-spacer></v-spacer>
            <v-btn style="margin: 5px" v-if="!userFollowing" v-on:click="followCurrentActivity()" color="primary" outlined rounded>Follow</v-btn>
            <v-btn style="margin: 5px" v-if="userFollowing" v-on:click="unFollowCurrentActivity()" elevation="0" color="primary" flat rounded filled>Un-follow</v-btn>
          </v-row>
        </v-card>
        <v-card :disabled="roleChanging" style="padding:20px;border-radius: 15px" class="activityContainer">
          <h3 style="font-size:13px;" v-if="!roleChanging">Involvement</h3>
          <v-skeleton-loader style="margin-bottom:2px;width: 100px;" v-if="roleChanging" ref="skeleton" type="text" ></v-skeleton-loader>
          <v-skeleton-loader v-if="roleChanging" ref="skeleton" type="heading" ></v-skeleton-loader>
          <h3 style="font-size:17px;font-weight: 500;" v-if="(userRole == 'none' || userRole == 'follower' || userRole == 'creator') && !roleChanging">Not Participating</h3>
          <h3 style="font-size:17px;font-weight: 500;" v-if="userRole == 'participant' && !roleChanging">You are a Participant</h3>
          <h3 style="font-size:17px;font-weight: 500;" v-if="userRole == 'organiser' && !roleChanging">You are an Organiser</h3>
          <v-skeleton-loader v-if="roleChanging" ref="skeleton" boilerplate="false" type="button"
                  style="position: absolute;right:20px;top:50%;transform:translateY(-50%);width:30px;height:30px;border-radius: 100px"
          ></v-skeleton-loader>
          <v-menu bottom left v-if="!roleChanging">
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                      style="position: absolute;right:20px;top:50%;transform:translateY(-50%);"
                      icon
                      v-bind="attrs"
                      v-on="on"
              >
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
            </template>

            <v-list>
              <v-list-item @click="roleSet('participant')" v-if="userRole == 'none' || userRole == 'organiser' || userRole == 'follower' || userRole == 'creator'">
                <v-list-item-title>Become a Participant</v-list-item-title>
              </v-list-item>
              <v-list-item @click="roleSet('organiser')" v-if="(userRole == 'none' || userRole == 'participant' || userRole == 'follower' || userRole == 'creator') && authorId === user.profile_id">
                <v-list-item-title>Become an Organiser</v-list-item-title>
              </v-list-item>
              <v-list-item @click="roleSet('none')" v-if="userRole == 'participant' || userRole == 'organiser'">
                <v-list-item-title>Clear Involvement</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-card>
        <AchievementsCard v-bind:achievements="achievements" :snackbar.sync="snackbar" :errorMessage.sync="errorMessage" />
      </div>
      <div id="activityPageCenter" class="activityPageColumn">
        <div>
              <v-card style="border-radius: 15px" class="activityPageCard">
                <h2>Participants & Organisers</h2>
                <v-tabs
                        v-model="previewTabs"
                        fixed-tabs
                        id="previewParticipantsOrganisersTabs"
                >
                  <v-tab
                          v-for="item in userTabs"
                          :key="item.tab"
                  >
                    {{ item.tab }}
                  </v-tab>
                </v-tabs>
                <v-tabs-items v-model="previewTabs" id="activityParticipantsOrganisersTabItems">
                  <v-tab-item
                          v-for="(item, index) in userTabs"
                          :key="index"
                  >
                    <v-card flat id="participantOrganiserList">
                      <div v-if="item.preview.length === 0">
                        <v-card-text>There are currently no {{ item.tab.toLowerCase() }} for this activity
                        </v-card-text>
                      </div>
                      <div v-else>
                        <v-list-item two-line v-for="(profile, index) in item.preview" :key="index" link
                                     @click.stop="">
                          <v-list-item-content>
                            <v-list-item-title v-if="profile.middlename != null">
                              {{ profile.firstname + " " + profile.middlename + " " +
                              profile.lastname}}
                            </v-list-item-title>
                            <v-list-item-title v-else>
                              {{ profile.firstname + " " + profile.lastname}}
                            </v-list-item-title>
                            <v-list-item-subtitle>{{ profile.email }}
                            </v-list-item-subtitle>
                          </v-list-item-content>
                          <div v-if="user.profile_id === authorId || user.permission_level > 0">
                            <v-menu
                                    transition="slide-y-transition"
                                    bottom
                                    right
                                    :close-on-click="true">
                              <template v-slot:activator="{ on, attrs }">
                                <v-btn
                                        v-bind="attrs"
                                        v-on="on"
                                        icon>
                                  <v-icon>mdi-dots-vertical</v-icon>
                                </v-btn>
                              </template>
                              <v-card>
                                <v-card>
                                  <v-list-item
                                          v-on:click="editUserActivityRole(item.tab.toLowerCase(), profile.email)">
                                    <div v-if="item.tab === 'Participants'">
                                      <v-list-item-title>Move to Organiser
                                      </v-list-item-title>
                                    </div>
                                    <div v-else>
                                      <v-list-item-title>Move to
                                        Participants
                                      </v-list-item-title>
                                    </div>
                                  </v-list-item>
                                </v-card>
                              </v-card>
                            </v-menu>
                          </div>
                        </v-list-item>
                      </div>
                    </v-card>
                  </v-tab-item>
                </v-tabs-items>
                <v-btn
                        id="activityPageShowMoreButton"
                        color="#1cca92"
                        outlined rounded
                        @click.stop="showMoreDialog = true"
                >Show More
                  </v-btn>
              </v-card>

            <v-dialog
                    v-model="showMoreDialog"
                    max-width="450"
                    id="activityPageMoreParticipantsOrganisersDialog"
            >
              <v-card style="border-radius: 15px" :loading="loadingParticipantsOrganisersDialog">
                <v-tabs
                        v-model="dialogTab"
                        fixed-tabs
                >
                  <v-tab
                          v-for="item in userTabs"
                          :key="item.tab"
                  >
                    {{ item.tab }}
                  </v-tab>
                </v-tabs>
                <v-tabs-items v-model="dialogTab">
                  <v-tab-item
                          v-for="item in userTabs"
                          :key="item.tab"
                  >
                    <div style="overflow-y: scroll; height: 500px"
                    >
                      <v-card flat
                      >
                        <div v-if="item.preview.length === 0">
                          <v-card-text>There are currently no {{
                            item.tab.toLowerCase() }} for this activity
                          </v-card-text>
                        </div>
                        <div v-else>
                          <v-list-item two-line v-for="(profile, index) in item.content"
                                       :key="index" link>
                            <v-list-item-content>
                              <v-list-item-title v-if="profile.middlename != null">
                                {{ profile.firstname + " " + profile.middlename + " "
                                + profile.lastname}}
                              </v-list-item-title>
                              <v-list-item-title v-else>
                                {{ profile.firstname + " " + profile.lastname}}
                              </v-list-item-title>
                              <v-list-item-subtitle>{{ profile.email }}
                              </v-list-item-subtitle>
                            </v-list-item-content>
                            <div v-if="user.profile_id === authorId || user.permission_level > 0">
                              <v-menu
                                      transition="slide-transition"
                                      bottom
                                      right
                                      :close-on-click="true"
                              >
                                <template v-slot:activator="{ on, attrs }">
                                  <v-btn
                                          v-bind="attrs"
                                          v-on="on"
                                          icon
                                  >
                                    <v-icon>mdi-dots-vertical</v-icon>
                                  </v-btn>
                                </template>
                                <v-card>
                                  <v-list-item
                                          v-on:click="editUserActivityRole(item.tab.toLowerCase(), profile.email)">
                                    <div v-if="item.tab === 'Participants'">
                                      <v-list-item-title>Move to Organiser
                                      </v-list-item-title>
                                    </div>
                                    <div v-else>
                                      <v-list-item-title>Move to
                                        Participants
                                      </v-list-item-title>
                                    </div>
                                  </v-list-item>
                                </v-card>
                              </v-menu>
                            </div>
                          </v-list-item>
                        </div>
                      </v-card>
                    </div>
                  </v-tab-item>
                </v-tabs-items>
                <v-row justify="center" no-gutters style="padding:15px 0;">
                  <v-card-text v-if="dialogTab === 0">
                    Showing {{userTabs[0].content.length}} out of {{numParticipants}} results
                  </v-card-text>
                  <v-card-text v-else>
                    Showing {{userTabs[1].content.length}} out of {{numOrganisers}} results
                  </v-card-text>
                  <v-btn
                          v-if="(dialogTab === 0 && (userTabs[0].content.length < 50 || user.permission_level > 0)) ||
                          dialogTab === 1"
                          color="primary"
                          outlined rounded
                          :loading="loadingParticipantsOrganisers"
                          v-on:click="getMoreResults()"
                  >More Results
                  </v-btn>
                </v-row>
              </v-card>
            </v-dialog>
          <v-card style="border-radius: 15px" v-if="visibility === 'restricted'" class="activityPageCard">
            <h2>Shared Users</h2>
            <form class="activityPageCardForm">
              <v-text-field v-model="emailsToAdd" class="activityPageCardTextField mb-5" label="Add email(s)"
                            outlined rounded clearable hide-details dense></v-text-field>
              <v-select class="activityPageCardSelect mr-10" v-model="newRole"
                        :items="roleOptions" name="roleValue" required label="Role" outlined hide-details dense
                        rounded></v-select>
              <v-btn v-on:click="parseEmails()" class="activityPageCardButton" height="40px" color="#1cca92"
                     outlined rounded>Add
              </v-btn>
              <h6 class="activityPageErrorMessage" v-if="displayInvalidInputError">{{ invalidInputErrorMessage
                }}
              </h6>
              <h6 class="editSuccessMessage" v-if="displaySharedUsersSuccessMsg">{{ sharedUsersStatusMsg }}
              </h6>
              <div id="usersCard" class="activityPageCardDiv">
                <v-card flat>
                  <v-list-item two-line v-for="user in sharedUsers" :key="user[0]">
                    <v-list-item-content>
                      <v-list-item-title v-if="user.middlename != null">
                        {{ user.firstname + " " + user.middlename + " " + user.lastname}}
                      </v-list-item-title>
                      <v-list-item-title v-else>
                        {{ user.firstname + " " + user.lastname}}
                      </v-list-item-title>
                      <v-list-item-subtitle>{{ user.primary_email }}</v-list-item-subtitle>
                    </v-list-item-content>
                  </v-list-item>
                </v-card>
              </div>
            </form>
          </v-card>
        </div>
      </div>
      <div id="activityPageRight" class="activityPageColumn">
          <v-card style="border-radius: 15px;min-height:0;" class="activityPageCard">
            <h2 style="padding-bottom:10px;">Latest Changes</h2>
            <v-timeline dense clipped v-for="(update, i) in activityChanges.data" :key="i">
              <v-timeline-item
                      icon-color="grey lighten-2"
                      small
              >
                <v-row justify="space-between">
                  <v-col>
                    <h2 style="font-size:14px;color:grey;font-weight:500;">{{formatDate(update.dateTime)}}</h2>
                    <ul>
                    <h2 v-for="(updateText, j) in update.textContext.split('*').slice(1)" :key="j"
                        style="font-size:15px;color:rgba(0,0,0,0.85);">
                      <li>{{updateText}}</li>
                    </h2>
                    </ul>
                  </v-col>
                </v-row>
              </v-timeline-item>
            </v-timeline>
            <v-card-text v-if="activityChanges.data.length == 0" style="text-align: center">No Changes Yet</v-card-text>
          </v-card>
        </div>
      <div class="floatClear"></div>
    </div>
  </div>
</template>

<script>

  import dateUtil from "@/util/date";
  import {mapActions, mapGetters} from "vuex";
  import {apiActivity, apiUser} from "../../api";
  import AchievementsCard from "./modules/AchievementsCard";
  import store from '@/store/index.js';

  export default {
    name: "ActivityPageInfo",
    components: {
        AchievementsCard,
    },
    data() {
      return {
        activity_name: "",
        activity_author_firstname: "",
        activity_author_lastname: "",
        continuous: false,
        description: "",
        activity_types: [],
        activityChanges: [],
        visibility: "",
        start_date: null,
        end_date: null,
        location: "",
        loaded: false,
        authorId: null,
        activityId: null,
        loadingActivity: true,
        userFollowing: null,
        previewTabs: null,
        dialogTab: null,
        showMoreDialog: false,
        snackbar: false,
        timeout: 2000,
        errorMessage: "",
        newRole: "participant",
        emailsToAdd: "",
        roleOptions: [
          {value: "participant", text: "Participant"},
          {value: "organiser", text: "Organiser"},
          {value: "follower", text: "Follower"}
        ],
        displayInvalidInputError: false,
        invalidInputErrorMessage: "",
        participantsPageInfo: {
          defaultPage: 0, currentPage: 0, defaultSize: 8, currentSize: 8,
        },
        organisersPageInfo: {
          defaultPage: 0, currentPage: 0, defaultSize: 8, currentSize: 8,
        },
        userTabs: [
          {tab: 'Participants', content: [], preview: []},
          {tab: 'Organisers', content: [], preview: []},
        ],
        loadingParticipantsOrganisers: false,
        loadingParticipantsOrganisersDialog: false,
        sharedUsers: [],
        displaySharedUsersSuccessMsg: false,
        displaySharedUsersErrorMsg: false,
        sharedUsersStatusMsg: "",
        currentPage: 0,
        size: 10,
        bottom: false,
        watching: true,
        numFollowers: 0,
        numParticipants: 0,
        numOrganisers: 0,
        userRole: "none",
        roleDisabled: true,
        roleChanging: false,
        achievements: [],
      }
    },

    computed: {
      ...mapGetters(['activity']),
      ...mapGetters(['user']),
    },

    beforeRouteEnter: (to, from, next) => {
      const activityId = to.params.activityId;
      const userId = store.state.user.user.profile_id;
      store.dispatch('checkUserActivityVisibility', {profileId: userId, activityId})
        .then(resp => {
          if (resp.data.visibility === 'allowed') {
            next()
          } else {
            next({name: "profilePage", params: {profileId: userId}})
          }
        })
        .catch(() => {
          next({name: "profilePage", params: {profileId: userId}})
        })
    },

    mounted: function () {
      if (!this.user.isLogin) {
        this.$router.push('/login');
      }
    },
    created: function () {
      this.loadActivity();
      this.getParticipants();
      this.getOrganisers();
      this.getStats();
      this.loadUserRole();
      return this.checkFollowing();
    },
    watch: {
      bottom(bottom) {
        if (bottom && this.watching) {
          this.currentPage += 1;
          apiActivity.getSharedUsers(this.activityId, this.currentPage, this.size).then(response => {
            if (response.data.content.length < this.size) {
              this.watching = false;
            }
            this.sharedUsers = this.sharedUsers.concat(response.data.content);
          })
        }
      }
    },
    methods: {
      ...mapActions(['updateUserDurationActivities', 'updateUserContinuousActivities', 'getActivityUpdates',
        'getParticipants', 'getOrganisers', 'checkUserActivityVisibility']),

      /**
       * Loads the role of the currently logged in user.
       */
      loadUserRole() {
        apiActivity.getUserRole(this.$route.params.activityId, this.user.profile_id)
        .then((response) => {
          this.userRole = response.data.role;
          this.roleDisabled = false;
        }).catch((err) => {
          this.errorMessage = err;
          this.snackbar = true;
          this.roleDisabled = false;
        });
      },

      /**
       * Set the role of the currently logged in user.
       */
      roleSet(role) {
        if(role != "none" && role != "participant" && role != "organiser"){
          this.errorMessage = "Invalid Role";
          this.snackbar = true;
          return;
        }
        this.roleChanging = true;
        if(role == "none"){
          apiActivity.optOutOfActivityRole(this.$route.params.activityId, this.user.primary_email)
          .then(() => {
            this.showMoreDialog = false;
            this.getOrganisers();
            this.getParticipants();
            this.roleChanging = false;
            this.userRole = role;
          }).catch((err) => {
            this.errorMessage = err;
            this.snackbar = true;
            this.roleChanging = true;
          });
        }else{
          apiActivity.editUserActivityRole(this.user.profile_id, this.$route.params.activityId, role, this.user.primary_email)
          .then(() => {
            this.showMoreDialog = false;
            this.getOrganisers();
            this.getParticipants();
            this.roleChanging = false;
            this.userRole = role;
          }).catch((err) => {
            this.errorMessage = err;
            this.snackbar = true;
            this.roleChanging = true;
          })
        }
      },

      /**
       * Format the received location string, ready to be displayed.
       */
      locationFormat(str) {
        let separated = str.split(",");
        let city = ""
        let state = ""
        let country = "";
        let outputString = "";

        if(typeof separated[0] !== 'undefined'){ city = separated[0] }
        if(typeof separated[1] !== 'undefined'){ state = separated[1] }
        if(typeof separated[2] !== 'undefined'){ country = separated[2] }

        if(city != ""){
          outputString += city.trim();
        }
        if(state != ""){
          if(outputString != ""){outputString += ", "}
          outputString += state.trim();
        }
        if(country != ""){
          if(outputString != ""){outputString += ", "}
          outputString += country.trim();
        }
        if(outputString == ""){
          outputString = "No Location Set"
        }
        return outputString;
      },

      /**
       * Checks if user has scrolled to bottom of card code sourced from: https://codepen.io/mednabouli/pen/EdKzzL
       */
      bottomVisible() {
        const cardScroll = document.getElementById("usersCard");
        const scrollY = cardScroll.scrollHeight - cardScroll.scrollTop;
        const height = cardScroll.offsetHeight;
        const offset = height - scrollY;
        return offset === 0;
      },

      /**
       * Parses the list of emails the user entered by splitting them and removing any extra spaces. Checks each one is
       * valid by calling validateEmail, and displays an error message stating which email is invalid if any.
       */
      async parseEmails() {
        this.displayInvalidInputError = false;
        const separators = [' ', ';'];
        let emails = this.emailsToAdd.split(new RegExp(separators.join('|'), 'g'));
        let emailsAreCorrect = true;
        for (let i = 0; i < emails.length; i++) {
          if (emails[i] === "") {
            emails.splice(i, 1);
            i--;
          }
        }
        for (let email of emails) {
          if (!this.validateEmail(email)) {
            this.invalidInputErrorMessage = "'" + email + "' is an invalid email address.";
            this.displayInvalidInputError = true;
            this.displaySharedUsersSuccessMsg = false;
            emailsAreCorrect = false;
            break; // So that the first invalid email is displayed
          }
        }
        if (emailsAreCorrect) {
          await apiActivity.setActivityMembers(emails, this.newRole, this.authorId, this.activityId)
            .then(response => {
              this.sharedUsersStatusMsg = response.data;
              this.displaySharedUsersSuccessMsg = true;
              this.currentPage = 0;
              this.getParticipants();
              this.getOrganisers();
              apiActivity.getSharedUsers(this.activityId, this.currentPage, this.size).then(response => {
                this.sharedUsers = response.data.content;
                this.watching = true;
              })
            })
            .catch(error => {
              this.errorMessage = error;
              this.snackbar = true;
              this.displaySharedUsersSuccessMsg = false;
              this.invalidInputErrorMessage = "Something went wrong, please check the information provided is correct.";
              this.displayInvalidInputError = true;
            })
        }
      },

      /**
       * Checks if an email address is valid. Adapted from
       * https://www.w3resource.com/javascript/form/email-validation.php
       */
      validateEmail(mail) {
        return /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(mail);
      },

      /**
       * Formats the datetime string to the form Aug 4 2020
       */
      formatDate(datetime) {
        let newDate = new Date(datetime);
        let dateString = newDate.toDateString();
        dateString = dateString.slice(4);
        return dateString;
      },

      /**
       * Retrieves participants for the activity
       */
      async getParticipants() {
        try {
          let response = await apiActivity.getParticipants(this.$route.params.activityId, this.participantsPageInfo.currentPage, this.participantsPageInfo.currentSize);
          this.userTabs[0].content = response.data.content;
          this.userTabs[0].preview = this.userTabs[0].content.slice(0, 3);
        } catch (err) {
          this.errorMessage = err;
          this.snackbar = true;
        }
      },

      async getStats() {
        await apiActivity.getActivityStats(this.$route.params.activityId).then(response => {
          this.numFollowers = response.data.followers;
          this.numOrganisers = response.data.organisers;
          this.numParticipants = response.data.participants;
        })
      },

      /**
       * Retrieves organisers for the activity
       */
      async getOrganisers() {
        try {
          let response = await apiActivity.getOrganisers(this.$route.params.activityId, this.organisersPageInfo.currentPage, this.organisersPageInfo.currentSize);
          this.userTabs[1].content = response.data.content;
          this.userTabs[1].preview = this.userTabs[1].content.slice(0, 3);
        } catch (err) {
          this.errorMessage = err;
          this.snackbar = true;
        }
      },

      /**
       * Retrieves more participants or organisers for the dialog box
       */
      async getMoreResults() {
        try {
          this.snackbar = false;
          this.loadingParticipantsOrganisers = true;
          if (this.dialogTab === 0) {
            let response = await apiActivity.getParticipants(this.$route.params.activityId, this.participantsPageInfo.currentPage + 1, this.participantsPageInfo.currentSize);
            if (response.data.content.length === 0) {
              this.errorMessage = "No more participants";
              this.snackbar = true;
            } else {
              this.userTabs[0].content = this.userTabs[0].content.concat(response.data.content);
              this.participantsPageInfo.currentPage += 1;
            }
            this.loadingParticipantsOrganisers = false;
          }
          if (this.dialogTab === 1) {
            let response = await apiActivity.getOrganisers(this.$route.params.activityId, this.organisersPageInfo.currentPage + 1, this.organisersPageInfo.currentSize);
            if (response.data.content.length === 0) {
              this.errorMessage = "No more organisers";
              this.snackbar = true;
            } else {
              this.userTabs[1].content = this.userTabs[1].content.concat(response.data.content);
              this.organisersPageInfo.currentPage += 1;
            }
            this.loadingParticipantsOrganisers = false;
          }
        } catch (err) {
          this.errorMessage = err;
          this.snackbar = true;
        }
      },

      /**
       * Edits a user's role from participant to organiser and vice versa
       */
      async editUserActivityRole(role, email) {
        if (this.user.profile_id === this.authorId || this.user.permission_level > 0) {
          let newRole = "";
          if (role === "participants") newRole = "organiser"; else newRole = "participant";
          this.loadingParticipantsOrganisersDialog = true;
          await apiActivity.editUserActivityRole(this.user.profile_id, this.$route.params.activityId, newRole, email)
            .then(() => {
              this.showMoreDialog = false;
              this.getOrganisers();
              this.getParticipants();
              this.roleChanging = false;
            }).catch((err) => {
                this.errorMessage = err;
                this.snackbar = true;
                this.roleChanging = true;
            })
          this.loadingParticipantsOrganisersDialog = false;
        }
      },

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
          this.errorMessage = err;
          this.snackbar = true;
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
          let tempActivityData = await apiActivity.getActivityById(this.$route.params.activityId);
          this.activityChanges = await apiActivity.getActivityUpdates(this.$route.params.activityId, 0, 5);
          if (tempActivityData === "Invalid permissions") {
            this.$router.push('/profile');
          } else {
            this.activityId = tempActivityData.id;
            this.activity_name = tempActivityData.activity_name;
            this.continuous = tempActivityData.continuous;
            this.description = tempActivityData.description;
            this.activity_types = tempActivityData.activity_type;
            this.visibility = tempActivityData.visibility;
            if (this.visibility === "restricted") {
              apiActivity.getSharedUsers(this.activityId, this.currentPage, this.size).then(
                (response) => {
                  this.sharedUsers = response.data.content;
                  document.getElementById("usersCard").addEventListener('scroll', () => {
                    this.bottom = this.bottomVisible()
                  });
                })
            }
            this.start_date = dateUtil.getFormatDate(new Date(tempActivityData.start_time));
            this.end_date = dateUtil.getFormatDate(new Date(tempActivityData.end_time));
            this.location = tempActivityData.location;
            this.activity_author_firstname = tempActivityData.author.firstname;
            this.activity_author_lastname = tempActivityData.author.lastname;
            this.authorId = tempActivityData.author.profile_id;
            this.loaded = true;
            this.loadingActivity = false;
            this.achievements = tempActivityData.achievements;
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
        await apiActivity.followActivity(this.user.profile_id, this.$route.params.activityId).then(response => {
          if (response.status === 201) {
            this.userFollowing = true;
            this.getStats();
          }
        });
      },

      /**
       * Makes api call to allow a user to un follow current activity after un follow button is pressed
       * @returns {Promise<void>}
       */
      async unFollowCurrentActivity() {
        await apiActivity.unfollowActivity(this.user.profile_id, this.$route.params.activityId).then(response => {
          if (response.status === 200) {
            this.userFollowing = false;
            this.getStats();
          }
        });
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/activityStyle.css";
  @import "../../../public/styles/pages/profileStyle.css";
</style>
