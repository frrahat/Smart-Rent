package com.frrahat.smartrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.frrahat.smartrent.model.ChatMessage;
import com.frrahat.smartrent.model.Status;
import com.frrahat.smartrent.model.UserType;
import com.frrahat.smartrent.utils.DatabaseHandler;
import com.frrahat.smartrent.utils.Driver;
import com.frrahat.smartrent.utils.Message;
import com.frrahat.smartrent.utils.MessageTypes;
import com.frrahat.smartrent.utils.TaxiRequest;
import com.frrahat.smartrent.widgets.Emoji;
import com.frrahat.smartrent.widgets.EmojiView;
import com.frrahat.smartrent.widgets.SizeNotifierRelativeLayout;
import com.frrahat.smartrent.widgets.SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MessageThreadActivity extends Activity 
implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, 
NotificationCenter.NotificationCenterDelegate {
		
	private String clientType;
	private HashMap<String, Integer> driverIDMap;
	private String requestID;
	private String clientID;
	private TaxiRequest taxiRequest;
	
	
	
	private ListView chatListView;
	private EditText chatEditText1;
	private ArrayList<ChatMessage> chatMessages;
	private ImageView enterChatView1, emojiButton;
	private ChatListAdapter listAdapter;
	private EmojiView emojiView;
	private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
	private boolean showingEmoji;
	private int keyboardHeight;
	private boolean keyboardVisible;
	private WindowManager.LayoutParams windowLayoutParams;
	
	InputMethodManager inputMethodManager;
	Query messagesQueryRef;
	
	private LatLng sourceLatLng;
	private LatLng destLatLng;
	
	private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    sendMessage(editText.getText().toString(), UserType.SELF);
                }

                chatEditText1.setText("");
                hideSoftKeyboard();

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {
                sendMessage(chatEditText1.getText().toString(), UserType.SELF);
            }

            chatEditText1.setText("");
            hideSoftKeyboard();

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }else{
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread);
		
		Intent intent=getIntent();
		String locationString=intent.getStringExtra("locationString");
		clientType=intent.getStringExtra("clientType");
		clientID=intent.getStringExtra("clientID");
		requestID=intent.getStringExtra("requestID");
		
		getTaxiRequest();
				
		getActionBar().setTitle("Destination : "+locationString);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		driverIDMap=new HashMap<String, Integer>();
		interfaceWithTheDatabase();
		
		
		AndroidUtilities.statusBarHeight = getStatusBarHeight();

        chatMessages = new ArrayList<>();

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

        // Hide the emoji on click of edit text
        chatEditText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingEmoji)
                    hideEmojiPopup();
            }
        });


        emojiButton = (ImageView)findViewById(R.id.emojiButton);

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });

        listAdapter = new ChatListAdapter(chatMessages, this);

        chatListView.setAdapter(listAdapter);

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        sizeNotifierRelativeLayout.delegate = (SizeNotifierRelativeLayoutDelegate) this;

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
		
	}
	
	
	private void getTaxiRequest(){
		Query queryRequestRef=DatabaseHandler.getRequestsRef(getApplicationContext())
				.orderByChild("requestID").equalTo(requestID);

		queryRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				taxiRequest=snapshot.child(requestID).getValue(TaxiRequest.class);
				if(listAdapter!=null)
		            listAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}
		});
	}
	/**
	 * 
	 */
	private void interfaceWithTheDatabase() {
		messagesQueryRef=DatabaseHandler.getMessagesRef(getApplicationContext())
				.orderByChild("requestID").equalTo(requestID);
		
		messagesQueryRef.addChildEventListener(new ChildEventListener() {
				
			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChild) {
				Message message=snapshot.getValue(Message.class);
				if(message.getAuthorID().equals(clientID)){//client himself
					if(message.getMessageType()==MessageTypes.TEXT){
						ChatMessage chatMessage = new ChatMessage(message.getMessageData(),
								UserType.SELF,Status.SENT,clientID, message.getTime());

						chatMessages.add(chatMessage);

				        if(listAdapter!=null)
				            listAdapter.notifyDataSetChanged();
					}
					else if(message.getMessageType()==MessageTypes.LatLng){
						updateSourceDestPoint(message.getMessageData());
					}
				}
				else{//other
					String author="";
					String authorID=message.getAuthorID();
					if(taxiRequest==null){
						author="**";
					}
					else if("driver".equals(clientType) && taxiRequest.getPassengerID().equals(authorID)){
						author="Passenger";
					}
					else {//now the other one is always a driver
						Integer id=driverIDMap.get(authorID);
						if(id==null){
							id=driverIDMap.size()+1;
							driverIDMap.put(authorID, id);
						}
						author="Driver-"+id;
					}
					if(message.getMessageType()==MessageTypes.TEXT){
						ChatMessage chatMessage = new ChatMessage(message.getMessageData(),
								UserType.OTHER,Status.SENT,author, message.getTime());
				        chatMessages.add(chatMessage);

				        if(listAdapter!=null)
				            listAdapter.notifyDataSetChanged();
					}
					
					else if(message.getMessageType()==MessageTypes.LatLng){
						//if("Passenger".equals(author)){
							updateSourceDestPoint(message.getMessageData());
						//}
					}
				}
			}

			private void updateSourceDestPoint(String data) {
				String parts[]=data.split(",");
				
				if(parts.length<4)
					return;
				
				sourceLatLng = new LatLng(Double.parseDouble(parts[0]), 
						Double.parseDouble(parts[1]));
				destLatLng = new LatLng(Double.parseDouble(parts[2]), 
							Double.parseDouble(parts[3]));
			}
			
			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				
			}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				
			}
			
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if(taxiRequest!=null && !taxiRequest.getIsAccepted()){
			Message message=new Message(requestID, MessageTypes.TEXT, "<leaving>", clientID, System.currentTimeMillis());
			DatabaseHandler.getNewID(DatabaseHandler.getMessagesRef(getApplicationContext()), null, message);
		}
        
        
        if("passenger".equals(clientType)){//deactivating request
        	Query queryRequestRef=DatabaseHandler.getRequestsRef(getApplicationContext())
    				.orderByChild("requestID").equalTo(requestID);

    		queryRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
    			
    			@Override
    			public void onDataChange(DataSnapshot snapshot) {
    				Firebase requestRef=snapshot.child(requestID).getRef();
    				requestRef.child("isAccepted").setValue(true);
    			}
    			
    			@Override
    			public void onCancelled(FirebaseError arg0) {
    				
    			}
    		});
        }
		super.onBackPressed();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thread, menu);
		if("driver".equals(getIntent().getStringExtra("clientType"))){
			((MenuItem)menu.findItem(R.id.action_call_driver)).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_show_in_map) {
			Intent intent=new Intent(MessageThreadActivity.this, LocationInMapActivity.class);
			if(sourceLatLng!=null){
				intent.putExtra("sLatitude", sourceLatLng.latitude);
				intent.putExtra("sLongitude", sourceLatLng.longitude);
			}
			if(destLatLng!=null){
				intent.putExtra("dLatitude", destLatLng.latitude);
				intent.putExtra("dLongitude", destLatLng.longitude);
			}
			if(sourceLatLng!=null || destLatLng!=null){
				startActivity(intent);
			}
			else{
				Toast.makeText(getApplicationContext(), "Location Parsing Failure", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		
		if(id == R.id.action_call_driver) {
			showCallListDialog();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
    
    
    private void sendMessage(final String messageText, final UserType userType)
    {
        /*if(messageText.trim().length()==0)
            return;*/
    	//pushing new message
        Message message=new Message(requestID, MessageTypes.TEXT, messageText, clientID, System.currentTimeMillis());
        DatabaseHandler.getNewID(DatabaseHandler.getMessagesRef(getApplicationContext()), null, message);

        /*// Mark message as delivered after one second

        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable(){
            @Override
            public void run(){
               message.setMessageStatus(Status.DELIVERED);

                final ChatMessage message = new ChatMessage();
                message.setMessageStatus(Status.SENT);
                message.setMessageText(messageText);
                message.setUserType(UserType.SELF);
                message.setMessageTime(new Date().getTime());
                chatMessages.add(message);

                MessageThreadActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });


            }
        }, 1, TimeUnit.SECONDS);*/

    }

    private Activity getActivity()
    {
        return this;
    }


    /**
     * Show or hide the emoji popup
     *
     * @param show
     */
    private void showEmojiPopup(boolean show) {
        showingEmoji = show;

        if (show) {
            if (emojiView == null) {
                if (getActivity() == null) {
                    return;
                }
                emojiView = new EmojiView(getActivity());

                emojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        chatEditText1.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText1.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, chatEditText1.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText1.setText(chatEditText1.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText1.setSelection(j, j);
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "Error showing emoji");
                        }
                    }
                });


                windowLayoutParams = new WindowManager.LayoutParams();
                windowLayoutParams.gravity = Gravity.BOTTOM | Gravity.START;
                if (Build.VERSION.SDK_INT >= 21) {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    windowLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
                }
                windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (keyboardHeight <= 0)
                keyboardHeight = getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = keyboardHeight;

            WindowManager wm = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);

            windowLayoutParams.height = currentHeight;
            windowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (emojiView.getParent() != null) {
                    wm.removeViewImmediate(emojiView);
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
            }

            try {
                wm.addView(emojiView, windowLayoutParams);
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                return;
            }

            if (!keyboardVisible) {
                if (sizeNotifierRelativeLayout != null) {
                    sizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        }
        else {
            removeEmojiWindow();
            if (sizeNotifierRelativeLayout != null) {
                sizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (sizeNotifierRelativeLayout != null) {
                            sizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }


    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (emojiView == null) {
            return;
        }
        try {
            if (emojiView.getParent() != null) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(emojiView);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }



    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {
        if (showingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return showingEmoji;
    }



    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    //@Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }

            if (chatListView != null) {
                chatListView.invalidateViews();
            }
        }
    }

    //@Override
    public void onSizeChanged(int height) {

        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }


        if (height > AndroidUtilities.dp(50) && keyboardVisible) {
            keyboardHeight = height;
            getSharedPreferences("emoji", 0).edit().putInt("kbd_height", keyboardHeight).commit();
        }


        if (showingEmoji) {
            int newHeight = 0;

            newHeight = keyboardHeight;

            if (windowLayoutParams.width != AndroidUtilities.displaySize.x || windowLayoutParams.height != newHeight) {
                windowLayoutParams.width = AndroidUtilities.displaySize.x;
                windowLayoutParams.height = newHeight;

                wm.updateViewLayout(emojiView, windowLayoutParams);
                if (!keyboardVisible) {
                    sizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeNotifierRelativeLayout != null) {
                                sizeNotifierRelativeLayout.setPadding(0, 0, 0, windowLayoutParams.height);
                                sizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && sizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!keyboardVisible && keyboardVisible != oldValue && showingEmoji) {
            showEmojiPopup(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    /**
     * Get the system status bar height
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideEmojiPopup();
    }
    
    private void hideSoftKeyboard(){
    	inputMethodManager.hideSoftInputFromWindow(chatEditText1.getWindowToken(), 0);
    }
    
    
    private void showCallListDialog(){
    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(MessageThreadActivity.this);
    	builderSingle.setIcon(R.drawable.ic_launcher);
    	builderSingle.setTitle("Select Callee:-");

    	final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
    	        MessageThreadActivity.this,
    	        android.R.layout.select_dialog_singlechoice);
    	
	    for(int i=0;i<driverIDMap.size();i++){
	    	arrayAdapter.add("Call Driver : "+Integer.toString(i+1));
	    }

    	builderSingle.setNegativeButton(
    	        "cancel",
    	        new DialogInterface.OnClickListener() {
    	            @Override
    	            public void onClick(DialogInterface dialog, int which) {
    	                dialog.dismiss();
    	            }
    	        });

    	builderSingle.setAdapter(
    	        arrayAdapter,
    	        new DialogInterface.OnClickListener() {
    	            @Override
    	            public void onClick(DialogInterface dialog, int position) {
    	            	for(String key : driverIDMap.keySet()){

    	            		if(driverIDMap.get(key)==position+1){
    	            			callDriver(key);
    	            			break;
    	            		}
    	            	}
    	            }
    	        });
    	builderSingle.show();
    }
    
    private void callDriver(final String driverID){
    	//Toast.makeText(getApplicationContext(), "Calling "+driverID, Toast.LENGTH_SHORT).show();
    	
    	Query driverQuery=DatabaseHandler.getDriversRef(getApplicationContext())
				.orderByChild("driverID").equalTo(driverID);
    	
    	driverQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Driver driver=snapshot.child(driverID).getValue(Driver.class);
				String number=driver.getPhoneNumber();
				//Toast.makeText(getApplicationContext(), number, Toast.LENGTH_SHORT).show();
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number)); 
		        startActivity(callIntent);
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {				
			}
		});
    }
}
