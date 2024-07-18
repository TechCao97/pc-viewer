# pc-viewer
An application which allows you view your pc directories, images, videos and files on your phone by explorer.

It's essentially a small springboot web server, with a starter code by java swing.

# start

1. Config the path which you want to view in the file "config.json". The config file must place in the same path with src or the jar package.

```json
{
  "paths": [
    {
      "path": "C:/Program Files",
      "lock": true
    },
    {
      "path": "C:/Program Files (x86)",
      "lock": false
    }
  ]
}
```

"Lock" means do you allow the client visit the upper path. For example, in the above config, you can't visit "C:" from the first item, but you can visit "C:" from the second item.

2. Run main from "cn.billycao.pcviewer.frame.IndexFrame".
3. Push start(启动) button.
4. Vist the path show in starter by your phone explorer.
5. Push end(终止) button to stop the server.
