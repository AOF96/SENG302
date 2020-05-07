<template>
    <div class="settingsNavContainer">
      <NavBar/>
      <h1>Settings</h1>
      <div class="navButtonContainer">
        <router-link  v-bind:to="'/settings/profile?u=' + this.$route.query.u"><div class="settingsNavButton">Profile</div></router-link>
        <hr>
        <router-link v-bind:to="'/settings/email?u=' + this.$route.query.u"><div class="settingsNavButton">Emails</div></router-link>
        <hr>
        <router-link v-bind:to="'/settings/password?u=' + this.$route.query.u"><div class="settingsNavButton">Password</div></router-link>
        <hr>
        <router-link to="/settings/passport_countries"><div class="settingsNavButton">Passport Countries</div></router-link>
        <hr>
        <router-link to="/settings/activities"><div class="settingsNavButton">Activities</div></router-link>
      </div>
    </div>
</template>

<script>
  import NavBar from "../../modules/NavBar";
  export default {
    components: {
        NavBar
    },
  }
</script>

<style scoped>
  @import '../../../../public/styles/pages/profileSettingsStyle.css';
</style>