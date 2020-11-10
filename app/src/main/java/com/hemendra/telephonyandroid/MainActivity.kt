package com.hemendra.telephonyandroid

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.SmsManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(),View.OnClickListener {
    var sms_permission:Boolean = false
    var call_permission:Boolean = false
    var file_uri:Uri? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        var call_status = ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.CALL_PHONE)
        var sms_status = ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)
         if(call_status == PackageManager.PERMISSION_GRANTED)
         {
             call_permission = true
         }
        if (sms_status== PackageManager.PERMISSION_GRANTED)
        {

            sms_permission = true
        }

        if(sms_status !=PackageManager.PERMISSION_GRANTED && call_status !=PackageManager.PERMISSION_GRANTED )
        {
            requestpermissions()
        }
        else if (sms_status != PackageManager.PERMISSION_GRANTED)
        {
            requestsmspermission()
        }
        else if (call_status != PackageManager.PERMISSION_GRANTED)
        {
            requestcallpermission()
        }
    }



    fun sendSMS()
    {
     if (sms_permission)
     {
         var smsManager:SmsManager = SmsManager.getDefault()
         smsManager.sendTextMessage(et_phone.text.toString(),null,et_msg.text.toString(),null,null)
     }
    }

    fun makeCall()
    {
        if(call_permission)
        {
            var i = Intent()
            i.action = Intent.ACTION_CALL
            i.data = Uri.parse("tel:"+et_phone.text.toString())
            startActivity(i)
        }

    }
    fun addAttachments()
    {
        var alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle("Pick a file")
        alertDialog.setPositiveButton("camera",object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {

               var i = Intent("android.media.action.IMAGE_CAPTURE")
                startActivityForResult(i,123)
            }
        })
        alertDialog.setNegativeButton("Files",object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
             var i = Intent()
                i.action = Intent.ACTION_GET_CONTENT
                i.type = "*/*"
                startActivityForResult(i,567)
            }
        })

        alertDialog.show()

    }
    fun sendemail()
    {

        var i = Intent()
        i.action = Intent.ACTION_SEND
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(et_email.text.toString()))
        i.putExtra(Intent.EXTRA_SUBJECT,et_sub.text.toString())
        i.putExtra(Intent.EXTRA_TEXT,et_body.text.toString())
        i.putExtra(Intent.EXTRA_STREAM,file_uri)
        i.setType("message/rfc822")
        startActivity(i)
    }

    fun init()
    {
        btn_call.setOnClickListener(this)
        btn_sms.setOnClickListener(this)
        btn_attachments.setOnClickListener(this)
        btn_email.setOnClickListener(this)

    }

    fun requestpermissions()
    {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS),1)
    }
    fun requestsmspermission()
    {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.SEND_SMS),2)
    }
    fun requestcallpermission()
    {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CALL_PHONE),3)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode)
        {
            1->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                 call_permission = true
                }
                if (grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    sms_permission= true
                }
            }

            2->{
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    sms_permission= true
                }
            }

            3->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    call_permission = true
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataIntent)
        if (requestCode==123 && resultCode== Activity.RESULT_OK)
        {
          var bmp:Bitmap = dataIntent!!.extras!!.get("data" ) as Bitmap
            file_uri = getimageuri(bmp)
            attach_img.setImageBitmap(bmp)

        }
        else if(requestCode==567 && resultCode==Activity.RESULT_OK)
        {
            file_uri = dataIntent!!.data
            attach_img.setImageURI(file_uri)
        }

    }
    override fun onClick(view: View?) {

        when(view?.id)
        {
            R.id.btn_sms ->{
                sendSMS()
            }
            R.id.btn_call ->{
                makeCall()
            }
            R.id.btn_email ->{
                sendemail()
            }
            R.id.btn_attachments ->{
                addAttachments()
            }


        }

    }


    fun getimageuri(bmp:Bitmap) :Uri
    {
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG,100,bytes)

        var path = MediaStore.Images.Media.insertImage(contentResolver,bmp,"test",null)

        return  Uri.parse(path)

    }

}