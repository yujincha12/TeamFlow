package com.example.pro1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar.FabAnchorMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.selects.select

class MainActivity : AppCompatActivity() {


    lateinit var projectList: ArrayList<ProjectModel>
    lateinit var adapter: ProjectAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var fab: FloatingActionButton
    lateinit var todoText: EditText
    lateinit var addBtn: Button

    lateinit var db: ProjectDB

    lateinit var bottomLayout: LinearLayout

    var gId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //DB

        db = ProjectDB(this)

        //초기화
        projectList = ArrayList()
        bottomLayout = findViewById(R.id.bottom_section)
        todoText = findViewById(R.id.todo_text)
        addBtn = findViewById(R.id.add_btn)
        fab = findViewById(R.id.fab)

        //recyclerView 설정
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //adapter 설정
        adapter = ProjectAdapter()
        adapter.setProject(projectList)

        //adapter 적용
        recyclerView.adapter = adapter

        //조회
        selectData()

        //등록모드
        fab.setOnClickListener {
            viewMode("ADD")
        }

        //추가 버튼
        addBtn.setOnClickListener {
            viewMode("FAB")

            var text = todoText.text.toString()

            //ADD면 등록 아니면 수정
            if(addBtn.text.toString() == "ADD") {//등록

                //데이터 담기
                val project = ProjectModel(0,text)

                //할 일 추가
                db.addProject(project)

                //조회 및 리셋
                selectReset("ADD")
            } else{ //수정
                //할 일 수정
 //               db.updateTask(gId, text)

                //조회 및 리셋
                selectReset("UPDATE")
            }

            //키보드 내리기
            hideKeyboard(todoText)
        }


        //할 일 입력 체크
        todoText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //텍스트 변경 시 실행

                if(charSeq.toString() == ""){
                    //비활성화
                    addBtn.isEnabled = false

                        //글씨 회색으로 변경
                    addBtn.setTextColor(Color.GRAY)
                }else{ //활성화
                    addBtn.isEnabled = true

                    //글씨 검정으로 변경
                    addBtn.setTextColor(Color.BLACK)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        /*
        //스와이프(수정, 삭제 가능)
        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //스와이프 기능
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.absoluteAdapterPosition

                when (direction) {
                    ItemTouchHelper.LEFT -> { //삭제

                        //할 일 변수에 담기
                        val id = taskList[position].id

                        //아이템 삭제
                        adapter.removeTask(position)

                        //DB에서 삭제
                        db.deleteTask(id)
                    }

                    ItemTouchHelper.RIGHT -> {//수정

                        viewMode("UPDATE")

                        //할 일 가져오기
                        val task = taskList[position].task

                        //할 일 ID 전역변수에 담기
                        gId = taskList[position].id

                        //입력창에 수정할 할 일 넣기
                        todoText.setText(task)

                        //버튼 문구 변경
                        addBtn.text = "UPDATE"

                    }
                }
            }

            //그리기
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )

                    .addSwipeLeftBackgroundColor(Color.RED) //왼쪽 스와이프 배경색 설정
                    .addSwipeLeftActionIcon(R.drawable.ic_delete) //왼쪽 스와이프 아이콘 설정
                    .addSwipeLeftLabel("삭제") //왼쪽 스와이프 라벨 설정
                    .setSwipeLeftLabelColor(Color.WHITE) //왼쪽 스와이프 라벨 색상

                    .addSwipeRightBackgroundColor(Color.BLUE) //오른쪽 스와이프 배경색 설정
                    .addSwipeRightActionIcon(R.drawable.ic_edit)//오른쪽 스와이프 아이콘 설정
                    .addSwipeRightLabel("수정")//오른쪽 스와이프 라벨 설정
                    .setSwipeRightLabelColor(Color.WHITE)//오른쪽 스와이프 라벨 색상
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(recyclerView)
*/
    }//onCreate()

    /**
     * 조회
     */
    private fun selectData(){
        //조회
        projectList = db.getAllProjects()

        //최신순 정렬
        projectList.reverse()

        //데이터 담기
        adapter.setProject(projectList)

        //적용
        adapter.notifyDataSetChanged()
    }
    /**
     * 조회 및 리셋
     */
    private fun selectReset(type: String){

        //조회
        selectData()

        //할일 입력 초기화
        todoText.setText("")

        //등록이 아니면 등록으로 변경
        if(type != "ADD"){
            addBtn.text = "ADD"
        }
    }
    private fun viewMode(type: String){

        //입력하고 나면 입력창 사라지고 FAB 보여줌
        if(type == "FAB"){
            //입력창 숨김
            bottomLayout.visibility = View.GONE

            //fab 보여줌
            fab.visibility = View.VISIBLE
        }else{
            //입력창 보여줌
            bottomLayout.visibility = View.VISIBLE

            //fab 숨김
            fab.visibility = View.INVISIBLE

        }
    }
    /**
     * 키보드 숨기기
     */
    private fun hideKeyboard(editText: EditText){

        val manager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //키보드 숨김


    }
}