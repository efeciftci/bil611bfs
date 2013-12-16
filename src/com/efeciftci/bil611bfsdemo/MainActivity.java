package com.efeciftci.bil611bfsdemo;

import java.util.LinkedList;
import java.util.Queue;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Menu;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private RelativeLayout rl;
	private Button btn_add_root;
	private Button btn_add_vertex;
	private Button btn_remove_vertex;
	private EditText et_value;
	private Button btn_setvalue;
	private EditText et_key;
	private Button btn_startbfs;
	private TextView tv_queue;

	private boolean flag_addRoot;
	private boolean flag_addVertex;
	private Vertex root;
	public Vertex selected;

	Paint paint;
	DrawView lines;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rl = (RelativeLayout) findViewById(R.id.relativeLayout1);
		btn_add_root = (Button) findViewById(R.id.button1);
		btn_add_vertex = (Button) findViewById(R.id.button2);
		btn_remove_vertex = (Button) findViewById(R.id.button3);
		et_value = (EditText) findViewById(R.id.editText1);
		btn_setvalue = (Button) findViewById(R.id.button4);
		et_key = (EditText) findViewById(R.id.editText2);
		btn_startbfs = (Button) findViewById(R.id.button5);
		tv_queue = (TextView) findViewById(R.id.textView1);

		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);

		lines = new DrawView(this);
		rl.addView(lines);

		flag_addRoot = false;
		flag_addVertex = false;

		/*
		 * bind enter key to run setVertexValue method while setting value of a
		 * vertex
		 */
		et_value.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					setVertexValue(v);
				}
				return false;
			}
		});

		/* bind enter key to run doBFS method after entering search key */
		et_key.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					doBFS(v);
				}
				return false;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_exit:
			System.exit(0);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/* TODO addRoot and addVertex repeat the same code, merge them */
		if (flag_addRoot) {
			/* runs when adding a root vertex for the first time */
			flag_addRoot = false;
			Point p = new Point((int) event.getX(), (int) event.getY());

			root = new Vertex(0, getApplicationContext(), p);
			root.tv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selected != null)
						selected.unselect();
					btn_add_vertex.setEnabled(true);
					btn_remove_vertex.setEnabled(true);
					et_value.setEnabled(true);
					btn_setvalue.setEnabled(true);
					et_key.setEnabled(true);
					btn_startbfs.setEnabled(true);
					selected = findVertexByTextView((TextView) v);
					selected.select();
					et_value.setText(String.valueOf(selected.getData()));
				}
			});
			rl.addView(root.tv);
		} else if (flag_addVertex) {
			/* runs when adding new child vertices */
			flag_addVertex = false;
			Point p = new Point((int) event.getX(), (int) event.getY());
			Vertex v = new Vertex(0, getApplicationContext(), p);
			selected.addChild(v);
			v.tv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selected != null)
						selected.unselect();
					btn_add_vertex.setEnabled(true);
					btn_remove_vertex.setEnabled(true);
					et_value.setEnabled(true);
					btn_setvalue.setEnabled(true);
					selected = findVertexByTextView((TextView) v);
					selected.select();
					et_value.setText(String.valueOf(selected.getData()));
				}
			});
			rl.addView(v.tv);
		} else {
			/* runs when touched on empty space */
			if (selected != null) {
				selected.unselect();
				selected = null;
				btn_add_vertex.setEnabled(false);
				btn_remove_vertex.setEnabled(false);
				et_value.setEnabled(false);
				btn_setvalue.setEnabled(false);
			}
		}
		return true;
	}

	/* adds starting vertex */
	public void addRoot(View v) {
		flag_addRoot = true;
		btn_add_root.setEnabled(false);
		Toast.makeText(this, R.string.toast_addroot, Toast.LENGTH_LONG).show();
	}

	/* adds child vertices for the chosen vertex */
	public void addVertex(View v) {
		flag_addVertex = true;
		/* TODO create a new string and add toast here */
	}

	/*
	 * compares available textView's in the layout and returns owning Vertex
	 * object if a match has been found
	 */
	public Vertex findVertexByTextView(TextView tv) {
		Queue<Vertex> q = new LinkedList<Vertex>();
		q.add(this.root);
		while (!q.isEmpty()) {
			Vertex v = q.remove();
			if (tv.equals(v.tv))
				return v;
			for (Vertex c : v.getChildren()) {
				q.add(c);
			}
		}
		return null;
	}

	/*
	 * reads value from EditText and sets value of selected vertex
	 */
	public void setVertexValue(View v) {
		int value = 0;
		try {
			value = Integer.parseInt(et_value.getText().toString());
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.toast_emptyvalue,
					Toast.LENGTH_LONG).show();
			return;
		}
		selected.setData(value);
	}

	/* breadth-first search is done here */
	public void doBFS(View v) {
		int key = 0;
		int cnt = 0;
		try {
			key = Integer.parseInt(et_key.getText().toString());
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.toast_emptykey,
					Toast.LENGTH_LONG).show();
			return;
		}
		resetTree();
		Queue<Vertex> q = new LinkedList<Vertex>();
		q.add(this.root);
		generateQStr(q);
		while (!q.isEmpty()) {
			Vertex vx = q.remove();
			cnt++;
			generateQStr(q);
			if (!vx.getVisited())
				vx.setVisited(true);
			if (key == vx.getData()) {
				vx.tv.setBackgroundColor(Color.RED);
				tv_queue.append("\nFound key=" + key + " in " + cnt + " steps.");
				return;
			}

			for (Vertex c : vx.getChildren()) {
				q.add(c);
				generateQStr(q);
			}
		}
		tv_queue.append("\nCould not find key=" + key + "!");
	}

	/* sets all vertices as unvisited */
	public void resetTree() {
		Queue<Vertex> q = new LinkedList<Vertex>();
		q.add(this.root);
		while (!q.isEmpty()) {
			Vertex v = q.remove();
			v.setVisited(false);
			for (Vertex c : v.getChildren()) {
				q.add(c);
			}
		}
		tv_queue.setText("");
	}

	/*
	 * generates queue string for each BFS enqueue / dequeue operation
	 */
	public void generateQStr(Queue<Vertex> q) {
		Vertex v;
		String qStr = new String("");
		for (int i = 0; i < q.size(); i++) {
			v = q.remove();
			qStr = qStr.concat(v.getData() + " ");
			q.add(v);
		}
		tv_queue.append("\nQueue: " + qStr);
	}

	/* draws lines between each relating vertices */
	private class DrawView extends View {
		public DrawView(Context c) {
			super(c);
		}

		@Override
		public void onDraw(Canvas canvas) {
			if (root != null) {
				Queue<Vertex> q = new LinkedList<Vertex>();
				q.add(root);
				while (!q.isEmpty()) {
					Vertex v = q.remove();
					for (Vertex c : v.getChildren()) {
						float startX = v.tv.getX() + v.tv.getWidth() / 2;
						float startY = v.tv.getY() + v.tv.getHeight() / 2;
						float stopX = c.tv.getX() + c.tv.getWidth() / 2;
						float stopY = c.tv.getY() + c.tv.getHeight() / 2;

						canvas.drawLine(startX, startY, stopX, stopY, paint);
						q.add(c);
					}
				}
			}
		}
	}

	public void removeVertex(View v) {
		/* TODO implement this */
		Toast.makeText(this, "To be implemented", Toast.LENGTH_LONG).show();
	}
}