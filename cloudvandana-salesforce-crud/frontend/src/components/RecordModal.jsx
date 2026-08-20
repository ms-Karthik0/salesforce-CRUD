import {useEffect,useState} from 'react';
export default function RecordModal({open,fields,record,onClose,onSave,mode}){
 const [form,setForm]=useState({});
 useEffect(()=>{setForm(record||{})},[record,open]); if(!open)return null;
 const editable=fields.filter(f=>!['Id','CreatedDate','CaseNumber'].includes(f));
 return <div className="overlay"><div className="modal"><div className="modalHead"><h2>{mode==='create'?'Create Record':mode==='view'?'View Record':'Edit Record'}</h2><button onClick={onClose}>×</button></div>
  {editable.map(f=><label key={f}>{f}<input value={form[f]??''} readOnly={mode==='view'} onChange={e=>setForm({...form,[f]:e.target.value})} /></label>)}
  <div className="actions"><button className="secondary" onClick={onClose}>Cancel</button>{mode!=='view'&&<button className="primary" onClick={()=>onSave(form)}>Save</button>}</div>
 </div></div>
}
