### How to create docker images 

#### Setup
1. change dir to parent folder of live-kitchen application with cd command  
`cd../../`  
2. create symbolic links to all files in dockerfile folder  
` ln -s live-kitchen/dockerfiles/* .`  


#### Create image using a specific dockerfile  

From parent folder of live-kitchen

Example 

`./build_live_kitchen_image.sh live_kitchen.Dockerfile `

#### Run container for image 

Example  

` docker run -p 8080:8080 live_kitchen_unpackjar_cds_aot_no_layers:latest`
