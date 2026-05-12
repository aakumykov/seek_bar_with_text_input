1) Inside Android Studio project:
```
git submodule add <module_name> git@github.com:aakumykov/seek_bar_with_text_input.git
```
2) In settings.gradle:
```
include ':<module_name>'
```
3) In consumer module build.gradle:
```
implementation project(':<module_name>')
```
4) In layout:
```
<com.github.aakumykov.seek_bar_with_text_input.SeekBarWithTextInput
        android:id="@+id/data_size_slider"
        android:layout_height="wrap_content"
        android:layout_width="match_parent"
        app:max="1073741824"
        app:progress="10485760"
    />
```
5) In code:
```
dataSizeSlider.apply {
    setChangeListener(object: SeekBarWithTextInput.ChangeListener{
        override fun onSeekBarWithTextInputProgressChanged(progress: Int, fromUser: Boolean) {
            Log.d(TAG, "progress: $progress")
        }
    })
    setProgressLabelProvider { progress ->
        getString(R.string.dataSize, "data size: $progress")
    }
}
```
