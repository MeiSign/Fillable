[![Build Status](https://travis-ci.org/MeiSign/Fillable.png?branch=master)](https://travis-ci.org/MeiSign/Fillable)

> Under Construction

# Fillable Autocompletion System
Fillable provides a simple Autocompletion System for Websites. It makes it easy to add autocompletion to any formfield.

- [What is Fillable?](#features)
- [Getting Started](#gettingStarted)
  - [Installation](#installation)
  - [Quickstep Guide](#quickstep)
  - [How to use an external Elasticsearch Cluster](#externalEs)

<a name="features" />
## What is Fillable?
Fillable provides a simple but clever Autocompletion System for Websites. It makes it easy to add autocompletion to any inputfield in your web formular.

### Learning System
Fillable uses the input of users to learn new autocompletion terms and weight them. It creates collections of terms which fit perfectly for your targetgroup.

### Mistake Tolerance
The autocompletion has a tolerance for spelling mistakes. This improves user experience by helping the user to find the correct spelling of a word.

### Easy to install and embed in your website
Fillable comes as Standalone Software and has a very simple installation process. You can create new Autocompletion fields and maintain created Fillable fields with a comfortable Webbackend.

### Fast Reaction
Autocompletion Userexperience is very depending on speed. Therefore Fillable provides a very fast reactiontime on user inputs.

### State of the Art Technology
Fillable uses internally one of the latest Elasticsearch Versions.

You can configure Fillable to use your own external Elasticsearch Cluster or let Fillable create its own Elasticsearch Cluster.

<a name="gettingStarted" />
## Getting started

<a name="installation"/>
### Install as Standalone Service (Windows & Unix)
1. Download the latest Fillable Version
2. Extract the archive to your Webserver
3. Enter the `bin` directory from terminal
4. Start Fillable with the `./fillable` command

> On Unix system you have to make the start script executable with `chmod +x fillable` before you can start it from Terminal.

You can access the Fillable Webbackend now with your browser on port 9000.

### Build from sources (Windows & Unix)
1. Clone this Git Repository
2. [Download](http://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip) & [Install](http://www.playframework.com/documentation/2.2.x/Installing) Play 2.2.1  
3. Enter the Fillable directory with Commandlinetool or terminal
4. Run Fillable with `play start` or `play run` for development mode

You can access the Fillable Webbackend now with your browser on port 9000.

<a name="quickstep"/>
### Quickstep Guide
This quickstep guide is supposed to show you how easy you can create an autocompletion form field with Fillable and
embed it into your website.

> A working Installation of Fillable is required too use this guide.
> Please go back and proceed with the installation notes if you haven't installed Fillable on your system yet.

Let's say you ran a website about cars and your want an autocompletion form field that help users with brandnames
(Hidden advertisements incoming ;-)).

The first step to your wonderful autocompletion form field ist to login to your Fillable Backend. You should be able to
reach it under http://127.0.0.1:9000/ if you are working on the machine where Fillable is installed.

After the login screen your are redirected to the Fillable Index List.
An Fillable Index is the data container of your autocompletion fields. Therefore you should see an empty list now what we will change in a second.

Click on Create Index under the list or in the main navigation to create our car brand field.
In the following form we use the default values and enter the name "car_brands". Please note that only lowercase characters are allowed in indexnames.

Click on create and you will be redirected to a page that shows you a basic html snippet. Just copy the snippet and paste in into a new html file on your computer for now.

Your new autocompletion field is ready to use now! Open the html file in your browser and enter "Audi" into the input field.
Click on submit, empty the textfield and start typing "Audi" again. You will see that it already gets suggested while you type.

These are the basics of Fillable. You can proceed with the following sections to get some additional information but your autocompletion is already working now.
Every userinput will be remembered and weigthed based on the frenquency of a term.

<a name="externalEs"/>
### How to use an external Elasticsearch Cluster
Using an external Elasticsearch Cluster might be very helpful when you want to use Fillable in production. If you have a lot of data
for your autocompletion field you must certainly want to use the great Scalability of Elasticsearch. The embedded Elasticsearch only
runs in a Single Node Cluster.

To use your own external Elasticsearch cluster you have to edit the Fillable config. You can find it inside the `conf` directory.

Change `esclient.embeddedElasticsearch=true` to `false`. And add one of your node addresses to the transport client urls.

`Fillable/conf/fillable.conf`

    # Embedded Elasticsearch
    # ~~~~~
    # This option determines if you use an external elasticsearch cluster or embedded elasticsearch node
    esclient.embeddedElasticsearch=false
    # Nonembedded Elasticsearch
    # ~~~~~
    # If you deactivate the embedded Elasticsearch node add the cluster urls which shall be used by
    # the elasticsearch transport client
    esclient.transportClientUrls=["127.0.0.1:9300"]

It is possible to add more than one node address to the config but usually not necessary. The client will sniff the other nodes.
However if you want to add several nodes you can do it like this

`esclient.transportClientUrls=["127.0.0.1:9300", "127.0.0.1:9301"]`