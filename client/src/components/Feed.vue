<template>
<div>
  <v-container class="my-5" style="max-width:800px;">
    <v-col>
      <v-list>
        <v-list-item
          v-for="(post, index) in activityPosts"
          :key="index"
        >
          <v-card class="mx-auto" rounded style="width:100%;max-width: 800px;margin-bottom:20px;">
            <v-card-text>
              <div>Activity Updated</div>
              <h2 class="text--primary py-2" style="font-weight:500;">{{ post.textContext }}</h2>
              <h3 class="text--primary">
                {{ post.dateTime }}
              </h3>
            </v-card-text>
            <v-card-actions>
              <v-list-item class="grow">
                <v-list-item-avatar color="grey darken-3">
                  <v-img
                    class="elevation-6"
                    src="https://avataaars.io/?avatarStyle=Transparent&topType=ShortHairShortCurly&accessoriesType=Prescription02&hairColor=Black&facialHairType=Blank&clotheType=Hoodie&clotheColor=White&eyeType=Default&eyebrowType=DefaultNatural&mouthType=Default&skinColor=Light"
                  ></v-img>
                </v-list-item-avatar>
                <v-list-item-content>
                  <v-list-item-title>{{ post.authorName }}</v-list-item-title>
                </v-list-item-content>
                <v-row
                  align="center"
                  justify="end"
                >
                  <v-btn class="px-3" :to="'/activity/'+post.activityId" text color="#00C853">View Changes</v-btn>
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
      defaultPage: 0,
      currentPage: 0,
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
