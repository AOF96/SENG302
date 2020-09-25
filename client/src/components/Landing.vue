<template>
  <div style="width: 100%;height: 100%;">
    <div v-bind:class="{'landingMainDiv': true, 'darkModeBanner': darkModeGlobal}">
      <div class="landingContainer">
        <img id="landingIcon" src="../../public/images/landingIcon.png"/>
        <div id="landingLeft">
          <h1 id="landingMainHeading">Passage</h1>
          <h2 id="landingSubHeading">The number one companion for any traveller. Create, manage, and explore activities near you. Start your journey today.</h2>
          <div id="landingButtonWrap">
            <button style="margin-left: 0;" class="landingButton loginHover" id="landingLoginButton" value="Login In" v-on:click="$router.push('/login')">Login</button>
            <button style="background: none;color:white;" class="landingButton signUpHover" id="landingSignUpButton" name="Sign Up" v-on:click="$router.push('/signup')">Sign Up</button>
          </div>
        </div>
        <div class="floatClear"></div>
      </div>
    </div>
    <div id="landingCenterWrap">
      <h1 id="landingAboutHeading">About Passage</h1>
      <h2 id="landingAboutDescription">
        Passage is the number one companion for any traveller. Regardless of your age or fitness level, Passage
        makes it easy to organise activities and share your interests with others. Passage allows you to create and
        manage your profile. You can specify what types of activities you like, your fitness level, and what
        countries you hold a passport in. You can also create activities that are either continuous or a set
        duration. Users can search for other users and activities to make connecting with others easy. You can
        follow activities and keep track of changes made to them in an intuitive home feed. Creators can add
        achievements to activities so participants can log their results and compete with each other. Passage has
        integration with Google Maps to make inputting locations simple. A global map is available to show all
        nearby activities.<br><br>

        Admins can perform just about any action on any user or activity to make managing Passage simple. Passage
        distinguishes itself from the competition through its focus on User Experience and mobile-friendliness.
        Passage's beautiful and intuitive design makes it easy to use on a desktop or mobile phone. With Passage,
        you are in control of your journey.
      </h2>
    </div>
    <div id="landingFooter">
      <h1 id="landingCopyRight">Copyright © Passage Dev. 2020</h1>
    </div>
  </div>
</template>

<script>
  export default {
    props: ['darkModeGlobal'],
    name: "Landing"
  }
</script>

<style scoped>
  @import "../../public/styles/pages/landingStyle.css";
</style>