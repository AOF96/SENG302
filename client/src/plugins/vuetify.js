import Vue from 'vue';
import Vuetify from 'vuetify/lib';

Vue.use(Vuetify);

export default new Vuetify({
    theme: {
        dark: false,
        options: {
            customProperties: true,
        },
        themes: {
            light: {
                primary: '#1cca92',
                secondary: '#dddddd',
                tertiary: '#dddddd',
                background: '#ffffff',
                component: '#ffffff',
                colouredComponent: '#3bb18b',
                primaryText: '#000000',
                secondaryText: '#1cca92',
                tertiaryText: '#ffffff',
                success: '#4CAF50',
                warning: '#FFC107',
                error: '#FF5252',
                info: '#2196F3',
            },
            dark: {
                primary: '#1cca92',
                secondary: '#4f4f4f',
                tertiary: '#333333',
                background: '#121212',
                component: '#1e1e1e',
                colouredComponent: '#238c6a',
                primaryText: '#ffffff',
                secondaryText: '#1cca92',
                tertiaryText: '#222222',
                success: '#4CAF50',
                warning: '#FFC107',
                error: '#FF5252',
                info: '#2196F3',
            }
        }
    }
});
