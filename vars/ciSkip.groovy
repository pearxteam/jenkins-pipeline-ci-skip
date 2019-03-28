import groovy.json.JsonSlurper

def call(String step) {
    if (step == 'check') {
        check()
    }
    else if (step == 'postProcess') {
        postProcess()
    }
    else {
        error 'ciSkip has been called without valid `step` argument'
    }
}

def check() {
    env.CI_SKIP = "false"
    if(currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').isEmpty()) {
        if (sh(script: "git log -1 | grep -i '.*\\[ci skip\\].*'", returnStatus: true) == 0) {
            env.CI_SKIP = "true"
            error "'[ci skip]' found in latest git commit message. Stopping the build."
        }
    }
}

def postProcess() {
    if (env.CI_SKIP == "true") {
        currentBuild.result = 'NOT_BUILT'
    }
}