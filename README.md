Original App Design Project - README Template
===

# Solidarity

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Allows citizens to easily browse and find political organization or support events in their area, or create one themselves for other users to attend. Citizens can more easily engage with political nearby events and causes that they identify with using this app.  

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Politics, Social Networking
- **Mobile:** Users can easily create an account and begin loggin events they are interested in, or that they like. Camera can be use to upload a profile picture, or to upload pictures for an event that has been created. 
- **Story:** Creates a way for users to easily find information on political events in their area, and see popular events or friends' interests as well. 
- **Market:** All politically engaged or interested individuals would benefir from using this app to better understand and participate in the political events in their area. 
- **Habit:** Users can create events for support at any time, especially near national holidays, celebrations, or elections.
- **Scope:** Users may log in and immediately see a feed of nearby events in their area (within an X mile radius), organized from most popular to least. They can click on events to get more information and to like or mark going on the event. They can navigate to their profile to see which events they have marked interested/going for, and can navigate to another page to create an event which will then be added to the feed. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* [ ] User can create an account on Solidarity app
* [ ] User can see a timeline of already created events in their area within a 60 mile radius after logging in.
* [ ] Timeline events are shown in order of most popular to least.
* [ ] User can create their own events and input a title, description, location, and perhaps pictures. Event includes creator name. 
* [ ] User can click on specific events to see a detailed view of them
* [ ] User can click on a "like/interested" button on each event, and on a "Going" button on the event. Double tap to like
* [ ] User can click on their profile to view or upload their own picture and see a display of their name below it.
* [ ] User can click on their profile where they can view events they have created.
* [ ] Google Maps SDK to filter events
* [ ] Material Design library implemented
* [ ] 

**Optional Nice-to-have Stories**

* [ ] User can use OAuth to log in with Facebook or Google
* [ ] User can click a share button on each event to share to another social media platform
* [ ] User can also view events they liked or clicked "goig" for under their profile
* [ ] User can choose which events they want to be "visible" on their profile
* [ ] User can click on names in event descriptions to view other user's profile
* [ ] Users can comment on event posts
* [ ] Filtering based on tags of posts (Ex: social activism, environmental activism, human rights activism, animal rights activism)
* [ ] Reformat into fragment based view
* ...

### 2. Screen Archetypes

* Login Screen
   * User can log in using a user + pass
   * ...
* Create Account Screen
   * User can create an account with a user/pass
   * ...
* Timeline Screen
   * User can view nearby events in a 60 mile radius on timeline screen, ordered from most popular to least.
   * User can click on an event to see a detail view 
* Create Event Screen
   * User can click icon in toolbar to create their own event and input a title, description, location, and perhaps pictures. Event includes crea
   * ...
* Event Detail View
   * User can view event details, and click one of two "Like" or "Going" options on it.
   * ...
* Profile Screen
   * User can see their own profile picture or upload one in their profile, along with their name.
   * Under this, they can see events they have created. 
   * ...

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Login Screen
* Create Account Screen
* Timeline Screen
* Create Event Screen
* Event Detail View Screen
* Profile View Screen

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Takes to create account screen if user does not have account
   * Takes to Timeline Screen if user has account and successfully logs in.
* Timeline Screen
   * Create Event Screen
   * Event Detail View Screen
   * Profile View Screen
   * 

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src=https://i.imgur.com/RXw1B2a.png width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models

#### User

| Property   |     Type      |  Description |
|----------|:-------------:|------:|
| objectId |  String | unique id for the user (default field) |
| username |    String   |   user username |
| password | String |    user password |
| createdAt | DateTime |    When user was created at (default field) |
| updatedAt | DateTime |    When user was updated at (default field) |

#### Event

| Property   |     Type      |  Description |
|----------|:-------------:|------:|
| objectId |  String | unique id for the event (default field) |
| author |    Pointer to User   |   user author of event |
| title | String |    title of event |
| image | File |    image inserted |
| description | String |    description of event |
| location | String |    location of event |
| likesArray | Array of User objects |    All users which liked event |
| commentsArray | Array of Comment Objects |    All comments inserted on post |
| createdAt | DateTime |    when event was created at (default field) |
| updatedAt | DateTime |    when event was created at (default field) |


#### Comment (Stretch)

| Property   |     Type      |  Description |
|----------|:-------------:|------:|
| objectId |  String | unique id for the comment (default field |
| author |    String   |   author of comment |
| content | String |    content of comment |
| createdAt | DateTime |    when comment was created at (default field) |
| updatedAt | DateTime |    when comment was updated at (default field) |


### Networking
#### Home Feed Screen
- Read/GET) Query all posts 
    - Ex: ParseQuery<Post> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_USER);
        query.whereEqualTo(Event.KEY_USER, ParseUser.getCurrentUser());
- (Create/POST) Create a new like on a post
- (Delete) Delete existing like
- (Create/POST) Create a new "going" on a post
- (Delete) Delete existing "going"
- (Create/POST) Create a new comment on a post
- (Delete) Delete existing comment


#### Create Event Screen
- (Create/POST) Create a new post object

#### Profile Screen
- (Read/GET) Query logged in user object
- (Update/PUT) Update user profile image
- (Read/GET) Query all posts where user is author

- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]

