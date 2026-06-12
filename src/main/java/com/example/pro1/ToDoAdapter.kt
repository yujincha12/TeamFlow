package com.example.pro1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView

class ToDoAdapter(): RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    lateinit var mDb: ToDoDB

    constructor(db: ToDoDB): this(){

        mDb = db
    }

    var todoList: ArrayList<ToDoModel> = ArrayList()


    /**
     * 화면 설정
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)

        val view: View = inflater.inflate(R.layout.task_item, parent, false)

        return ViewHolder(view)

    }

    /**
     * 데이터 설정
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //아이템 닫기
       val item: ToDoModel = todoList[position]

        //할일 내용
        holder.mCheckBox.text = item.task

        //상태값이 0이 아니면 체크
        holder.mCheckBox.isChecked = toBooelan(item.status)

        //체크박스 체크 이벤트
        holder.mCheckBox.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{

            override fun onCheckedChanged(comButton: CompoundButton?, isCheck: Boolean) {

                //체크 상태면 1 아님 0
                if(isCheck){
                    mDb.updateStatus(item.id, 1)
                }else{
                    mDb.updateStatus(item.id, 0)
                }
            }
        })
    }

    /**
     * 상태값으로 체크상태 알아내기
     * 상태값이 0이 아니면 true 0이면 false
     */
    private fun toBooelan(n: Int) = n !=0


    /**
     * 데이터 개수 가져오기
     */
    override fun getItemCount(): Int = todoList.size

    /**
     *할일 목록 담기
     */
    fun setTask(todoList: ArrayList<ToDoModel>){

        this.todoList = todoList
        notifyDataSetChanged()
    }

    /**
     * 할 일 삭제
     */
    fun removeTask(position: Int){
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val mCheckBox: CheckBox = view.findViewById(R.id.m_check_box)
    }

}