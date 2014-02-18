[![Build Status](https://travis-ci.org/MeiSign/Fillable.png?branch=master)](https://travis-ci.org/MeiSign/Fillable)


# Fillable Autocompletion System
Fillable provides a simple Autocompletion System for Websites. It makes it easy to add autocompletion to any formfield.

## Features
### Learning System
Fillable uses the input of users to learn new autocompletion terms and weight the terms. It creates collections of terms which fit directly for your website and user demands.

### Mistake Tolerance
The autocompletion has a tolerance for spelling mistakes. This improves user experience by helping the user to find the correct spelling of a word.

### Easy to install and embed in your website
Fillable comes as Standalone Software and has a very simple installation process. You can create new Autocompletion fields and maintain created Fillable fields with a comfortable Webbackend.

### Fast Reaction
Autocompletion Userexperience is very depending on speed. Therefore Fillable provides a very fast reactiontime on user inputs.

### State of the Art Technology
Fillable uses internally one of the latest Elasticsearch Versions.

You can configure Fillable to use your own external Elasticsearch Cluster or let Fillable create its own Elasticsearch Cluster.

## Installation

### Install as Standalone Service
1. Download the latest Fillable Version
2. Extract the archive to your Webserver
3. Enter the bin directory from terminal
4. Make the start script executable with 'chmod +x fillable'
4. Start Fillable with the './fillable' command

You can access the Fillable Webbackend now with your browser on port 9000.

### Build from sources
1. Clone this Git Repository
2. [Download](http://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip) & [Install](http://www.playframework.com/documentation/2.2.x/Installing) Play 2.2.1  
3. Enter the Fillable Directory
4. Run Fillable with play start

You can access the Fillable Webbackend now with your browser on port 9000.