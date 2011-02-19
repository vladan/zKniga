package com.gemokodero.zk.utils;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.provider.ContactsContract;

import com.gemokodero.zk.core.Result;

public final class AddContact
{
    private Account[] mAccounts;
    private AccountData mSelectedAccount;
    
    private Context mContext;
    private Result mResult;
    
    private int mPhoneType;
    private int mEmailType;

    public AddContact(Result result, AccountData data, Context context)
    {
    	mPhoneType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
    	mEmailType = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
    	
        mAccounts = AccountManager.get(context).getAccounts();
        mSelectedAccount = data;
               
        mContext = context;
        mResult = result;
    }

    public boolean createContactEntry() {

    	String name = mResult.getName();
        String phone = mResult.getPhoneNumber();
        String website = mResult.getWebsite();
   
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, mSelectedAccount.getType())
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, mSelectedAccount.getName())
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, mPhoneType)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Website.DATA, website)
                .withValue(ContactsContract.CommonDataKinds.Website.TYPE, mEmailType)
                .build());

        ContentProviderResult[] result = null;
        try {
        	result = mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        boolean success = false;
		if(result!=null) {
        	success  = true;
        }
        
        return success;
    }
}