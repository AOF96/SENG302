<template>
<div>
  <v-container class="my-5" style="max-width:800px;">
    <v-col>
      <v-list>
        <v-list-item
          v-for="(post, index) in activityPosts"
          :key="index"
        >
          <v-card class="mx-auto" rounded style="width:100%;max-width: 700px;margin-bottom:20px;border-radius: 15px;">
            <v-card-text>
              <v-row no-gutters>
                <div>Activity Updated</div>
                <v-spacer></v-spacer>
                <div>{{ formatDate(post.dateTime) }}</div>
              </v-row>
              <h2 class="text--primary py-2" style="font-weight:500;font-size: 18px">Activity '{{ post.activityName }}' was edited.</h2>
              <ul>
                <li v-for="(update, i) in post.textContext.split('*').slice(1)" :key="i">{{update.trim()}}</li>
              </ul>
            </v-card-text>
            <v-card-actions>
              <v-list-item class="grow">
                <v-list-item-avatar>
                  <v-img v-bind:src="rootLocation+'images/userIcon.png'"></v-img>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title style="font-size: 15px">{{ post.authorName }}</v-list-item-title>
                </v-list-item-content>
                <v-row
                  align="center"
                  justify="end"
                >
                  <v-btn class="px-3" :to="'/activity/'+post.activityId" text color="#00C853">View Activity</v-btn>
                </v-row>
              </v-list-item>
            </v-card-actions>
          </v-card>
        </v-list-item>

        <v-row no-gutters align="center" justify="center">
          <v-progress-circular v-if="isLoading"
                  indeterminate
                  color="primary"
          />
          <v-card-text style="text-align: center; color: grey" v-if="isFinished">No More Results</v-card-text>
        </v-row>
      </v-list>
    </v-col>
  </v-container>

</div>
</template>

<script>
import {mapGetters, mapState, mapActions} from "vuex";

export default {
  name: "Feed",
  components: {

  },
  computed: {
    ...mapState(["user", "userSearch"]),
    ...mapGetters(["user", "userSearch"])
  },
  data: function() {
    return {
      rootLocation: process.env.VUE_APP_BASE_URL,
      activityPosts: [],
      activityPostsTemplate: [
        {
          postType: "activityUpdate",
          datetime: "2020-07-30 15:49:30.384000",
          userId: 20022,
          userName: "Easter Bunny",
          activityId: 1,
          activityName: 'Easter Egg Hunt',
          textContent: 'Start time was changed from 6:30AM to 10:00AM and the description was updated.',
        },
        {
          postType: "activityUpdate",
          datetime: "2020-07-30 18:06:10.204000",
          userId: 20022,
          userName: "Easter Bunny",
          activityId: 1,
          activityName: 'Easter Egg Hunt',
          textContent: 'Activity activity types changed to: [Fun]',
        }
      ],
      defaultPage: 0,
      currentPage: 0,
      defaultSize: 10,
      currentSize: 10,
      bottom: false,
      isLoading: false,
      isFinished: false
    };
  },
  mounted() {
    this.checkLogin();
    this.getUsersFeed();
    window.addEventListener('scroll', () => {
      this.bottom = this.bottomVisible();
    })
  },
  watch: {
    bottom(bottom) {
      if (bottom) {
        this.getUsersFeed();
      }
    }
  },
  methods: {
    ...mapActions(["getUserFeed"]),
    checkLogin() {
      if (!this.user.isLogin) {
        this.$router.push('/login');
      }
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
    getUsersFeed() {
      if (!this.isLoading && !this.isFinished) {
        this.isLoading = true;
        this.getUserFeed({'id': this.user.profile_id, 'page': this.currentPage, 'size': this.currentSize})
                .then((response) => {
                  this.activityPosts = this.activityPosts.concat(response.data);
                  this.isLoading = false;
                  this.currentPage++;
                  if (response.data.length === 0) {
                    this.isFinished = true;
                  }
                })
                .catch((error) => {
                  console.log(error);
                  this.isLoading = false;
                })
      }
    },

    bottomVisible() {
      const scrollY = window.scrollY;
      const visible = document.documentElement.clientHeight;
      const pageHeight = document.documentElement.scrollHeight - 150;
      const bottomOfPage = visible + scrollY >= pageHeight;
      return bottomOfPage || pageHeight < visible
    },
  }
};
</script>

<style scoped>
@import "../../public/styles/pages/feedStyles.css";
</style>
