asdfsadf# Tyr
  
![Powered by OpenShift Online](https://www.openshift.com/images/logos/powered_by_openshift.png)

[![Build Status](https://travis-ci.org/jboss/tyr.svg?branch=master)](https://travis-ci.org/jboss/tyr)

Pull request template status check

## Development

1. Build with Maven - mvn clean install
1. Create a testing repository - for start you can fork my 
https://github.com/xstefank/test-repo where the PR template is set up or 
you can create your own :)
    - For creating new repository check the github manuals/help
    - PRs needs to be created to YOUR repository, you need to specify it 
    under the base fork field in the Open a Pull Request page
1. The project requires two properties to be set up to run correctly
    - GitHub OAuth token - 
        - in `config.properties` file (will be described later)
        - or by the `GITHUB_OAUTH_TOKEN` environment property
    - Configuration file specifying the format of the PR - the example can 
    be found in `tyr-runner/src/main/resources/format-example.yaml`, there are
    two ways how to specify to the application where is this file located
        - System property **template.format.url** - url of the configuration file
        - System property **template.format.file** -  path to the configuration file
1. start Tyr - `java -jar tyr-runner.jar` with above system properties set
1. Expose local server instance with ngrok - https://ngrok.com/
    - Here you need to expose **http** port **8080** as the default application
    interface where the application runs, consult the ngrok documentation for the
    information of how to specify these arguments
1. Now you should have everything prepared locally, next step is to let 
GitHub know where your Tyr instance runs
1. Add a webhook for Tyr
    - In you test-repo - go to Settings -> Webhooks -> Add webhook
    - Fill in Payload URL -> *your local exposed IP which is provided by 
    ngrok + “/pull-request”* (e. g., http://f202cf7c.ngrok.io/pull-request)
    - Fill in Content type -> application/json
    - Set *Let me select individual events* and check only *Pull requests* 
    and *Pull request review comments*
    - Leave Active set and click “Add webhook”
    - Don’t worry about the error - the first JSON differs from what the 
    subsequent PR JSONs will look like
1. If you create a PR now (create a new branch based on master for instance) 
you should already see some output in the server logs
    - Be careful to which repository and branch you are doing the PR!!! 
    You need to specify your repository, the point is to try to set up Tyr 
    on your repository.
1. However, you will still probably see problems with the custom PR check 
which the app is trying to send to GitHub as there is a requirement to 
provide an oauth token to the GitHub API
1. Go to your profile -> settings (top right) -> Developer Settings -> 
Personal access tokens
1. Generate new token
1. Tick repo:status
1. Generate token
1. The last step is to let Tyr know what is the token value
    - add the token to **config.properties** file (the example is provided in 
    the `tyr-runner/src/main/resources` folder) and copy this file to the same directory
    where Tyr is running
1. Restart Tyr
1. Rerun (update existing or create new PR) - everything should be working now
1. If everything runs ok the PR should be updated with the valid error or 
green color, and in terminal where the server runs you’ll see 
**Status update: 201**

## OpenShift deployment

This project contains an openshift profile to be easily deployable to
[Openshift platform](https://www.openshift.com/). 

To deploy to Openshift:

1. login to your OS account (`oc login`)
1. `mvn fabric8:deploy -Popenshift`
