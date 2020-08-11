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
                  <v-img

                    src="/images/userIcon.png"
                  ></v-img>
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
      defaultPage: 1,
      currentPage: 1,
      defaultSize: 10,
      currentSize: 10,
    };
  },
  mounted() {
    this.checkLogin();
    this.getUsersFeed();
  },
  watch: {

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
      this.getUserFeed({'id': this.user.profile_id, 'page': this.currentPage, 'size': this.currentSize})
      .then((response) => {
        console.log(response.data);
        this.activityPosts = response.data;
      })
      .catch((error) => {
        console.log(error);
      })
    }
  }
};
</script>

<style scoped>
@import "../../public/styles/pages/feedStyles.css";
</style>
