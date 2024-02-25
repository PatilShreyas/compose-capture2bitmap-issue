# Jetpack Compose's @Composable content to Bitmap conversion issue Demo

A sample to demonstrate issues observed around the guide for ["Write contents of a composable to a bitmap"](https://developer.android.com/jetpack/compose/graphics/draw/modifiers#composable-to-bitmap) 
when the network image _(like `AsyncImage` from Coil-kt)_ is used.

Issue: https://issuetracker.google.com/issues/305653364

## Issues

### Issue 1

In this issue, content is not getting drawn on the UI initially when a Modifier for capturing the content of composable to bitmap is used.
But, content gets drawn on a UI when the `Picture` is converted to a `Bitmap`. 

https://github.com/PatilShreyas/compose-capture2bitmap-issue/assets/19620536/875c2ff2-7719-4ad3-a803-ce77efd8c544

### Issue 2

Like the above issue, content is not getting drawn on the UI initially but starts getting rendered when app is paused and resumed again.

https://github.com/PatilShreyas/compose-capture2bitmap-issue/assets/19620536/0017ffe2-c4fd-40f3-864c-fed7139eb320

