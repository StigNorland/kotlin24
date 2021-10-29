package no.nsd.qddt.model.interfaces

import java.util.*

interface  IHaveParent<T:IBasedOn>: IBasedOn {

//    @Column(insertable = false, updatable = false)
    var parentId: UUID?

    var parentIdx: Int?

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parentId")
    var parent: T?

}

interface IHaveChilden<T:IBasedOn>  {

//    @OrderColumn(name = "parentIdx",  updatable = false, insertable = false)
//    @AuditMappedBy(mappedBy = "parentId", positionMappedBy = "parentIdx")
//    @OneToMany(mappedBy = "parentId" ,  cascade = [CascadeType.REMOVE, CascadeType.PERSIST] )
    var children: MutableList<T>

    fun addChild(entity:T ): T

//    children.add(entity)
//    entity.parent = this
//    changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
//    changeComment =  String.format("{} [ {} ] added", entity.classKind, entity.name)
//    return entity

}

