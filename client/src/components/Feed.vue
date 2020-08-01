<template>
<div>
  <v-container class="my-5" style="max-width:800px;">
    <v-col>
      <v-card class="mx-auto" rounded>
        <v-card-text>
          <div>Activity Updated</div>
          <h2 class="text--primary py-2" style="font-weight:500;">Easter Egg Hunt</h2>
          <h3 class="text--primary">
            Start time was changed from 6:30AM to 10:00AM and the description was updated.
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
              <v-list-item-title>Karna Malone</v-list-item-title>
            </v-list-item-content>

            <v-row
              align="center"
              justify="end"
            >
              <v-btn class="px-3" text color="#00C853">View Changes</v-btn>
            </v-row>
          </v-list-item>

        </v-card-actions>
      </v-card>
    </v-col>
  </v-container>

</div>
</template>

<script>
import {mapGetters, mapState, mapActions} from "vuex";
// import {apiUser} from "../../api";

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
      defaultPage: 1,
      currentPage: 1,
      defaultSize: 10,
      currentSize: 10,
    };
  },
  mounted() {
    if (!this.user.isLogin) {
      this.$router.push('/login');
    }
  },
  watch: {

  },
  methods: {
    ...mapActions(["getUserFeed"]),

  }
};
</script>

<style scoped>
@import "../../public/styles/pages/feedStyles.css";
</style>
