### How to create docker images 

1. change dir to parent folder of live-kitchen application with cd command  
`cd../../`  
2. create symbolic links to all files in dockerfile folder  
` ln -s live-kitchen/dockerfiles/* .`  

From parent folder of live-kitchen  
Create desired image using the desired dockerfile

Example 

`./build_live_kitchen_image.sh live_kitchen.Dockerfile `
