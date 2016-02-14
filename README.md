# FlowLayout
[ ![Download](https://api.bintray.com/packages/misakuo/maven/FlowLayout/images/download.svg) ](https://bintray.com/misakuo/maven/FlowLayout/_latestVersion)

****  
###简介
FlowLayout是一个基于ViewGroup编写的控件，可以将一组View以从左到右从上到下的顺序展示，支持以Adapter的方式注入数据。
###UI效果
![snapshot](https://raw.githubusercontent.com/misakuo/FlowLayout/master/snapshot.gif)    
###使用
- 在`build.gradle`中添加  
```
compile 'com.moxun:flowlayoutlib:1.0.0'
```
- 在布局文件中引入    
```
<com.moxun.flowlayoutlib.FlowLayout/>
```    
可以像其他任何ViewGroup一样对其设置布局属性，包括长宽Padding、Margin值等    
 - 设置Adapter    
 继承`FlowItemAdapter`，并实现以下方法     
 **public int getItemCount()**    
 返回Item数量    
 **public Object getItem(int position)**    
 返回position对应的数据    
 **public View getView(Context context, ViewGroup parent, int position)**    
 返回position对应的View    
 - 通知数据变化    
 当数据发生变化时，需要调用Adapter的以下方法来通知ViewGroup更新        
 **notifyItemInserted(int position)**     
 position位置插入一个数据时调用，当对应的View显示时会附加动画（如果有设置Insert动画）    
 **notifyItemRemoved(int position)**  
 position位置的数据被移除时调用，当对应的View移除时会附加动画（如果有设置Remove动画）    
 **notifyItemChanged(int position)**      
 position位置的数据发生变化（Item数量未发生变化）时调用，仅会对对应View进行更新，不会附加动画    
 **notifyDataSetChanged()**   
 DataSet内的数据发生变化时调用，ViewGroup会重绘所有Item，并且不会附加动画（无论有没有动画）    
 - 监听Item点击事件    
 Way1. 可以在Adapter的`getView()`方法中为每一个View设置`OnClickListener`    
 Way2. 也可以通过设置OnItemClickListener的方式来对FlowLayout内每一个child的点击事件进行监听，实现`FlowLayout.OnItemClickListener`，在`public void onItemClick(ViewGroup parent, View view, int position, long id)`方法中处理点击事件，***注意：当设置了OnItemClickListener之后，所有在getView()方法中设置的OnClickListener都将失效***    
 - 设置Item动画        
 **setItemInsertAnimation(Animation insertAnim)**    
 设置Item被插入时的动画        
 **setItemRemoveAnimation(Animation removeAnim)**    
 设置Item被移除时的动画    
 此处可以使用两个预设值`FlowLayout.DEFAULT_INSERT_ANIMATION`和`FlowLayout.DEFAULT_REMOVE_ANIMATION`


