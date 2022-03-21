job('MNTLAB-nprohorov-main-build-job') {

  description 'Central DSL Job'
  label 'master'
  parameters {
    gitParam('Branch') {
      description 'The Git branch'
      type 'BRANCH'
      defaultValue 'origin/master'
    }
    activeChoiceReactiveParam('childe_name') {
           description('Choose job number')
           choiceType('CHECKBOX')
           groovyScript {
               script('return ["MNTLAB-nprohorov-child1-build-job", "MNTLAB-nprohorov-child2-build-job", "MNTLAB-nprohorov-child3-build-job", "MNTLAB-nprohorov-child4-build-job"]')
           }
    }
  }
  
  scm {
    git {
      remote {
        url 'https://github.com/Nikita-Prohorov/simple-java-maven-app.git'
      }
      branch '$Branch'
    }
  }
  steps {
       downstreamParameterized {
               trigger('$childe_name') {
                 block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                 }
                 parameters {
                     currentBuild()
                 }
           }
       }
   }
}
def List_Jobs = ["MNTLAB-nprohorov-child1-build-job", "MNTLAB-nprohorov-child2-build-job", "MNTLAB-nprohorov-child3-build-job", "MNTLAB-nprohorov-child4-build-job"]
for(job in List_Jobs) {

mavenJob(job) {
  description 'Child job'
  label 'master'
  parameters {
    gitParam('Branch') {
      description 'The Git branch'
      type 'BRANCH'
    }
    activeChoiceReactiveParam('childe_name') {
           description('Choose job number')
           choiceType('CHECKBOX')
           groovyScript {
               script('return ["MNTLAB-nprohorov-child1-build-job", "MNTLAB-nprohorov-child2-build-job", "MNTLAB-nprohorov-child3-build-job", "MNTLAB-nprohorov-child4-build-job"]')
           }
    }
  }  

  scm {
    git {
      remote {
        url 'https://github.com/Nikita-Prohorov/simple-java-maven-app.git'
      }
      branch '$Branch'
    }
  }


  rootPOM 'pom.xml'
  goals 'clean install'
  postBuildSteps {
    shell('nohup java -jar target/${BUILD_NUMBER}-1.jar com.test >> loglist.log')
        shell('tar -cvf "$(echo $Branch | cut -d "/" -f 2)_dsl_script.tar.gz" target/*.jar loglist.log')
    }
  
  publishers {
        archiveArtifacts('*.tar.gz')
    }
}
}
