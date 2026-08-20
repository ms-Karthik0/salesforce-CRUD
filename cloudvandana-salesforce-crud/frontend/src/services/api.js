const BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
async function request(path, options={}) {
  const res = await fetch(BASE + path, {credentials:'include', headers:{'Content-Type':'application/json',...(options.headers||{})}, ...options});
  const text = await res.text(); let data = text ? JSON.parse(text) : null;
  if(!res.ok) throw new Error(data?.error || data?.message || `Request failed (${res.status})`);
  return data;
}
export const api={
 status:()=>request('/auth/status'), login:()=>{window.location.href=BASE+'/auth/login'}, logout:()=>request('/auth/logout',{method:'POST'}),
 objects:()=>request('/salesforce/objects'), fields:o=>request(`/salesforce/objects/${o}/fields`), list:(o,p)=>request(`/salesforce/objects/${o}?page=${p}`),
 create:(o,b)=>request(`/salesforce/objects/${o}`,{method:'POST',body:JSON.stringify(b)}), update:(o,id,b)=>request(`/salesforce/objects/${o}/${id}`,{method:'PATCH',body:JSON.stringify(b)}), delete:(o,id)=>request(`/salesforce/objects/${o}/${id}`,{method:'DELETE'})
};
