const sonarqubeScanner =  require('sonarqube-scanner');
sonarqubeScanner(
    {
        // replace "X" hereunder to match your VM url
        serverUrl:  'https://csse-s302g3.canterbury.ac.nz/sonarqube/',
        token: "adfe77e527b386ab17408df1908ed6822947d75a",
        options : {
            'sonar.projectKey': 'team-300-client',
            'sonar.projectName': 'Team 300 - Client',
            "sonar.sourceEncoding": "UTF-8",
            'sonar.sources': 'src',
            'sonar.tests': 'src',
            'sonar.inclusions': '**',
            'sonar.test.inclusions': 'src/**/*.spec.js,src/**/*.test.js,src/**/*.test.ts',
            'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.javascript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml'
        }
    }, () => {});