<template>

  <v-container fluid grid-list-md fill-height fill-width>
    <v-layout row wrap width="600px">
      <v-flex>
        <v-card class="activityContainer" :loading="loadingActivity">
          <h3 id="activityPageTitle" class="activityTitle"> {{ activity_name }} </h3>
          <div id="activityPageDescription" class="activityDescriptionLabel">{{ description }}</div>
          <div id="activityPageVisibility" class="activityLocationLabel">
            {{ "Visibility: " + visibility.replace(/\b\w/g, l => l.toUpperCase()) }}
          </div>
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
          </div>
        </v-card>
      </v-flex>

      <v-flex>
        <v-flex>
          <v-layout row wrap>
            <v-flex>
              <v-card class="activityPageCard">
                <h2>Participants / Organisers</h2>
                <v-tabs
                        v-model="tab"
                        fixed-tabs
                >
                  <v-tab
                          v-for="item in userTabs"
                          :key="item.tab"
                  >
                    {{ item.tab }}
                  </v-tab>
                </v-tabs>

                <v-tabs-items v-model="tab">
                  <v-tab-item
                          v-for="item in userTabs"
                          :key="item.tab"
                  >
                    <v-card flat>
                      <v-list-item two-line v-for="user in item.content.slice(0, 3)" :key="user.profile_id" link @click.stop="">
                        <v-list-item-content>
                          <v-list-item-title v-if="user.middlename != null">
                            {{ user.firstname + " " + user.middlename + " " + user.lastname}}
                          </v-list-item-title>
                          <v-list-item-title v-else>
                            {{ user.firstname + " " + user.lastname}}
                          </v-list-item-title>
                          <v-list-item-subtitle>{{ user.primary_email }}</v-list-item-subtitle>
                        </v-list-item-content>
                        <v-menu
                                transition="slide-y-transition"
                                bottom
                                right
                                :close-on-click="false"
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
                            <v-card-text>Change Role</v-card-text>
                            <v-switch v-model="roleSwitch"></v-switch>
                          </v-card>
                        </v-menu>

                      </v-list-item>
                    </v-card>
                  </v-tab-item>
                </v-tabs-items>

                <v-btn
                       class="activityPageShowMoreButton"
                       height="40px" color="#1cca92"
                       outlined rounded
                       @click.stop="showMoreDialog = true"
                >Show More</v-btn>
              </v-card>
            </v-flex>

            <v-dialog
              v-model="showMoreDialog"
              max-width="450"
            >
              <v-tabs
                v-model="tab"
                fixed-tabs
              >
                <v-tab
                        v-for="item in userTabs"
                        :key="item.tab"
                >
                  {{ item.tab }}
                </v-tab>
              </v-tabs>

              <v-tabs-items v-model="tab">
                <v-tab-item
                  v-for="item in userTabs"
                  :key="item.tab"
                >
                  <div style="overflow-y: scroll; height: 500px"
                  >
                    <v-card flat
                    >
                      <v-list-item two-line v-for="user in item.content" :key="user.profile_id" link>
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
                </v-tab-item>
              </v-tabs-items>
            </v-dialog>

            <v-flex>
              <v-card class="activityPageCard">
                <h2>Map track</h2>
                <h3>Coming next sprint!</h3>
              </v-card>
            </v-flex>

          </v-layout>
        </v-flex>

        <v-flex>
          <v-layout row wrap>
            <v-flex>
              <v-card class="activityPageCard">
                <h2>Gallery</h2>
                <h3>Coming at some stage!</h3>
              </v-card>
            </v-flex>

            <v-flex>
              <v-card class="activityPageCard" style="min-height:0;">
                <h2 style="padding-bottom:10px;">Updates</h2>
                <v-timeline dense clipped v-for="(update, i) in activityChanges" :key="i">
                  <v-timeline-item
                    class="mb-4"
                    icon-color="grey lighten-2"
                    small
                  >
                    <v-row justify="space-between">
                      <v-col cols="7">
                        <h2 style="font-size:16px;color:grey;font-weight:500;">{{formatDate(update.date)}}</h2>
                        <h2 style="font-size:16px;color:rgba(0,0,0,0.85);">{{update.description}}</h2>
                      </v-col>
                    </v-row>
                  </v-timeline-item>
                </v-timeline>
              </v-card>
            </v-flex>
          </v-layout>
        </v-flex>

        <v-flex>
          <v-layout row wrap>
            <v-flex>
              <v-card v-if="visibility === 'restricted'" class="activityPageCard">
                <h2>Shared Users</h2>
                <form class="activityPageCardForm">
                    <v-text-field v-model="emailsToAdd" class="activityPageCardTextField mb-5" label="Add email(s)"
                                  outlined rounded clearable hide-details dense></v-text-field>
                    <v-select class="activityPageCardSelect mr-10" v-model="newRole"
                              :items="roleOptions" name="roleValue" required label="Role" outlined hide-details dense
                              rounded></v-select>
                    <v-btn v-on:click="parseEmails()" class="activityPageCardButton" height="40px" color="#1cca92"
                           outlined rounded>Add</v-btn>
                    <h6 class="activityPageErrorMessage" v-if="displayInvalidEmailError">{{ invalidEmailErrorMessage }}
                    </h6>
                  <div class="activityPageCardDiv">
                    <v-card flat>
                      <v-list-item two-line v-for="user in sharedUsers" :key="user.profile_id">
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
            </v-flex>
          </v-layout>
        </v-flex>
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
        activityChanges: [{"description": "this is a test", "date": "2020-08-04 15:19:00"}],
        visibility: "restricted",
        start_date: null,
        end_date: null,
        location: "",
        loaded: false,
        authorId: null,
        activityId: null,
        loadingActivity: true,
        tab: null,
        showMoreDialog: false,
        newRole: "participant",
        emailsToAdd: "",
        roleOptions: [
            { value: "participant", text: "Participant" },
            { value: "organiser", text: "Organiser"},
            { value: "follower", text: "Follower"}
        ],
        displayInvalidEmailError: false,
        invalidEmailErrorMessage: "",
        participants: [],
        organisers: [],
        defaultPage: 1,
        currentPage: 1,
        defaultSize: 10,
        currentSize: 10,
        // participants: [
        //   {
        //     "bio":"I'm a cool guy???!",
        //     "authoredActivities":[],
        //     "profile_id":23010,
        //     "firstname":"Jackie",
        //     "lastname":"Qiu",
        //     "middlename":"J",
        //     "gender":"Male",
        //     "nickname":"JackDog",
        //     "date_of_birth":"1999-10-21",
        //     "fitness":2,
        //     "city":null,
        //     "state":null,
        //     "country":null,
        //     "passports":["New Zealand","Australia"],
        //     "activities":["Team-Sport","Fun","Relaxing"],
        //     "primary_email":"jqi26@uclive.ac.nz",
        //     "additional_email":["coolmail@gmail.com","radmail@mail.com"],
        //     "permission_level":1
        //   },
        // ],
        // organisers: [
        //   {
        //     "bio":"I'm a cool guy???!",
        //     "authoredActivities":[],
        //     "profile_id":23010,
        //     "firstname":"Jackie",
        //     "lastname":"Qiu",
        //     "middlename":"J",
        //     "gender":"Male",
        //     "nickname":"JackDog",
        //     "date_of_birth":"1999-10-21",
        //     "fitness":2,
        //     "city":null,
        //     "state":null,
        //     "country":null,
        //     "passports":["New Zealand","Australia"],
        //     "activities":["Team-Sport","Fun","Relaxing"],
        //     "primary_email":"jqi26@uclive.ac.nz",
        //     "additional_email":["coolmail@gmail.com","radmail@mail.com"],
        //     "permission_level":1
        //   }
        // ],
        userTabs: [
          { tab: 'Participants', content: null },
          { tab: 'Organisers', content: null },
        ],
        sharedUsers: [
        {
          "bio":"I'm a cool guy???!",
          "authoredActivities":[],
          "profile_id":23010,
          "firstname":"Jackie",
          "lastname":"Qiu",
          "middlename":"J",
          "gender":"Male",
          "nickname":"JackDog",
          "date_of_birth":"1999-10-21",
          "fitness":2,
          "city":null,
          "state":null,
          "country":null,
          "passports":["New Zealand","Australia"],
          "activities":["Team-Sport","Fun","Relaxing"],
          "primary_email":"jqi26@uclive.ac.nz",
          "additional_email":["coolmail@gmail.com","radmail@mail.com"],
          "permission_level":1
        },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },
          {
            "bio":"I'm a cool guy???!",
            "authoredActivities":[],
            "profile_id":23010,
            "firstname":"Jackie",
            "lastname":"Qiu",
            "middlename":"J",
            "gender":"Male",
            "nickname":"JackDog",
            "date_of_birth":"1999-10-21",
            "fitness":2,
            "city":null,
            "state":null,
            "country":null,
            "passports":["New Zealand","Australia"],
            "activities":["Team-Sport","Fun","Relaxing"],
            "primary_email":"jqi26@uclive.ac.nz",
            "additional_email":["coolmail@gmail.com","radmail@mail.com"],
            "permission_level":1
          },]
      }
    },

    computed: {
      ...mapGetters(['activity']),
      ...mapGetters(['user']),
    },
    mounted: function () {
        if (!this.user.isLogin) {
            this.$router.push('/login');
        } else {
            //this.activityChanges = this.getActivityUpdates(this.$route.params.activityId).data;
            this.getParticipants();
            this.getOrganisers();
        }
    },
    created: function () {
      this.loadActivity();
      this.userTabs[0].content = this.participants;
      this.userTabs[1].content = this.organisers;
    },
    methods: {
      ...mapActions(['updateUserDurationActivities','updateUserContinuousActivities','getActivityUpdates', 'getParticipants', 'getOrganisers']),

      /**
       * Parses the list of emails the user entered by splitting them and removing any extra spaces. Checks each one is
       * valid by calling validateEmail, and displays an error message stating which email is invalid if any.
       */
      parseEmails() {
        this.displayInvalidEmailError = false;
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
              this.invalidEmailErrorMessage = "'" + email + "' is an invalid email address.";
              this.displayInvalidEmailError = true;
              emailsAreCorrect = false;
              break; // So that the first invalid email is displayed
            }
        }
        if (emailsAreCorrect) {
          apiActivity.setActivityMembers(emails, this.newRole, this.authorId, this.activityId);
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
      async getParticipants() {
        try {
          let response = await apiActivity.getParticipants(this.$route.params.activityId, this.currentPage, this.currentSize);
          this.participants = response.data;
        } catch (err) {
          console.error(err)
        }
      },
      async getOrganisers() {
        try {
          let response = await apiActivity.getOrganisers(this.$route.params.activityId, this.currentPage, this.currentSize);
          this.organisers = response.data;
        } catch (err) {
          console.error(err)
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
            // TODO: Uncomment this line once the server response includes the visibility
            //this.visibility = tempActivityData.visibility;
            if (this.visibility === "restricted") {
              /* TODO: Add code here to make a call to api.js method to retrieve all shared users for this activity
              and set it to sharedUsers
               */
            }
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
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/activityStyle.css";
</style>
