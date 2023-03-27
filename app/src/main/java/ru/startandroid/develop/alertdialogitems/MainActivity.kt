package ru.startandroid.develop.alertdialogitems

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListAdapter

const val LOG_TAG = "myLogs"

class MainActivity : AppCompatActivity() {
    val DIALOG_ITEMS = 1
    val DIALOG_ADAPTER = 2
    val DIALOG_CURSOR = 3

    var cnt = 0
    var db: DB? = null
    var cursor: Cursor? = null

    val data = arrayOf("one", "two", "three", "four")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DB(this)
        db!!.open()
        cursor = db!!.getAllDate()
        startManagingCursor(cursor)
    }
    fun onClick(v:View) {
        changeCount()
        when(v.id) {
            R.id.btnItems -> showDialog(DIALOG_ITEMS)
            R.id.btnAdapter -> showDialog(DIALOG_ADAPTER)
            R.id.btnCursor -> showDialog(DIALOG_CURSOR)
            else -> {}
        }
    }

    override fun onCreateDialog(id: Int): Dialog {
        val adb = AlertDialog.Builder(this)
        when(id) {
            DIALOG_ITEMS -> {
                adb.setTitle(R.string.items)
                adb.setItems(data, myClickListener)
            }
            DIALOG_ADAPTER -> {
                adb.setTitle(R.string.adapter)
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                    android.R.layout.select_dialog_item, data)
                adb.setAdapter(adapter, myClickListener)
            }
            DIALOG_CURSOR -> {
                adb.setTitle(R.string.cursor)
                adb.setCursor(cursor, myClickListener, COLUMN_TXT)
            }
        }
        return adb.create()
    }

    override fun onPrepareDialog(id: Int, dialog: Dialog?) {
        val aDialog:AlertDialog = dialog as AlertDialog
        val lAdapter:ListAdapter = aDialog.listView.adapter
        when(id) {
            DIALOG_ITEMS, DIALOG_ADAPTER -> {
                if (lAdapter is BaseAdapter) {
                    val bAdapter: BaseAdapter = lAdapter as BaseAdapter
                    bAdapter.notifyDataSetChanged()
                }
            }
            DIALOG_CURSOR -> {}
        }
    }

    var myClickListener: DialogInterface.OnClickListener = object :
        DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            Log.d(LOG_TAG, "which = $which")
        }
    }

    fun changeCount() {
        cnt++
        data[3] = cnt.toString()
        db!!.changeRec(4, cnt.toString())
        cursor!!.requery()
    }

    override fun onDestroy() {
        super.onDestroy()
        db!!.close()
    }
}

