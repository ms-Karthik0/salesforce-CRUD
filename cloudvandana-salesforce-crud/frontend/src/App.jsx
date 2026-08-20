import {useEffect,useRef,useState} from 'react';
import {api} from './services/api';
import RecordModal from './components/RecordModal';
import './styles.css';
const objs=['Account','Opportunity','Lead','Contact','Case'];
export default function App(){
 const [auth,setAuth]=useState(false),[object,setObject]=useState('Account'),[fields,setFields]=useState([]),[rows,setRows]=useState([]),[page,setPage]=useState(0),[loading,setLoading]=useState(false),[hasMore,setHasMore]=useState(true),[modal,setModal]=useState(null),[error,setError]=useState('');
 const sentinel=useRef(null);
 useEffect(()=>{api.status().then(x=>setAuth(x.authenticated)).catch(()=>{});},[]);
 useEffect(()=>{if(!auth)return; setError('');setRows([]);setPage(0);setHasMore(true);api.fields(object).then(x=>setFields(x.fields)).catch(e=>setError(e.message));},[auth,object]);
 useEffect(()=>{if(!auth||!hasMore)return; setLoading(true);api.list(object,page).then(d=>{const r=d.records||[];setRows(prev=>page===0?r:[...prev,...r]);setHasMore(r.length===20);}).catch(e=>setError(e.message)).finally(()=>setLoading(false));},[auth,object,page]);
 useEffect(()=>{if(!auth)return;const io=new IntersectionObserver(es=>{if(es[0].isIntersecting&&!loading&&hasMore)setPage(p=>p+1)},{rootMargin:'400px'});if(sentinel.current)io.observe(sentinel.current);return()=>io.disconnect()},[auth,loading,hasMore]);
 async function save(form){try{if(modal.mode==='create'){await api.create(object,form)}else await api.update(object,modal.record.Id,form);setModal(null);setRows([]);setPage(0);setHasMore(true)}catch(e){setError(e.message)}}
 async function remove(id){if(!confirm('Delete this record from Salesforce?'))return;try{await api.delete(object,id);setRows(r=>r.filter(x=>x.Id!==id))}catch(e){setError(e.message)}}
 if(!auth)return <div className="login"><div className="card"><div className="logo">SF</div><h1>Salesforce CRUD Console</h1><p>Manage Account, Opportunity, Lead, Contact and Case records from one React + Spring Boot application.</p><button className="primary big" onClick={api.login}>Login with Salesforce</button><small>OAuth 2.0 · Secure server-side token handling</small></div></div>;
 return <div className="app"><header><div><h1>Salesforce CRUD Console</h1><span>CloudVandana Associate Software Engineer Assignment</span></div><button className="secondary" onClick={async()=>{await api.logout();setAuth(false)}}>Logout</button></header>
 <main><div className="toolbar"><select value={object} onChange={e=>setObject(e.target.value)}>{objs.map(o=><option key={o}>{o}</option>)}</select><button className="primary" onClick={()=>setModal({mode:'create',record:{}})}>+ Create {object}</button></div>
 {error&&<div className="error">{error}</div>}
 <div className="tableWrap"><table><thead><tr>{fields.map(f=><th key={f}>{f}</th>)}<th>Actions</th></tr></thead><tbody>{rows.map(r=><tr key={r.Id}>{fields.map(f=><td key={f}>{String(r[f]??'—')}</td>)}<td><div className="rowActions"><button onClick={()=>setModal({mode:'view',record:r})}>View</button><button onClick={()=>setModal({mode:'edit',record:r})}>Edit</button><button className="danger" onClick={()=>remove(r.Id)}>Delete</button></div></td></tr>)}</tbody></table>{!rows.length&&!loading&&<div className="empty">No records found.</div>}<div ref={sentinel} className="sentinel">{loading?'Loading next 20 records…':hasMore?'Scroll for more':'All loaded'}</div></div></main>
 <RecordModal open={!!modal} fields={fields} record={modal?.record} mode={modal?.mode} onClose={()=>setModal(null)} onSave={save}/></div>
}
